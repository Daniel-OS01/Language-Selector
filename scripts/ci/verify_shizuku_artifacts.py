#!/usr/bin/env python3
"""Verify that the configured Shizuku client artifacts exist on Maven Central."""

from __future__ import annotations

import argparse
import time
import tomllib
from dataclasses import dataclass
from pathlib import Path
from typing import Callable
from urllib.error import HTTPError, URLError
from urllib.parse import quote
from urllib.request import Request, urlopen


DEFAULT_CATALOG = Path("gradle/libs.versions.toml")
DEFAULT_REPOSITORY = "https://repo1.maven.org/maven2"
SHIZUKU_ALIASES = ("shizuku-api", "shizuku-provider")
TRANSIENT_HTTP_STATUSES = {408, 429, 500, 502, 503, 504}


class VerificationError(RuntimeError):
    """Raised when the catalog or a configured artifact is invalid."""


@dataclass(frozen=True)
class Artifact:
    group: str
    name: str
    version: str

    @property
    def coordinate(self) -> str:
        return f"{self.group}:{self.name}:{self.version}"

    def pom_url(self, repository: str) -> str:
        group_path = "/".join(quote(part, safe="") for part in self.group.split("."))
        encoded_name = quote(self.name, safe="")
        encoded_version = quote(self.version, safe="")
        return (
            f"{repository.rstrip('/')}/{group_path}/{encoded_name}/{encoded_version}/"
            f"{encoded_name}-{encoded_version}.pom"
        )


def load_shizuku_artifacts(catalog_path: Path) -> tuple[Artifact, ...]:
    try:
        with catalog_path.open("rb") as catalog_file:
            catalog = tomllib.load(catalog_file)
    except (OSError, tomllib.TOMLDecodeError) as error:
        raise VerificationError(f"Cannot read version catalog {catalog_path}: {error}") from error

    versions = catalog.get("versions")
    libraries = catalog.get("libraries")
    if not isinstance(versions, dict) or not isinstance(libraries, dict):
        raise VerificationError(f"{catalog_path} must contain [versions] and [libraries] tables")

    artifacts: list[Artifact] = []
    for alias in SHIZUKU_ALIASES:
        library = libraries.get(alias)
        if not isinstance(library, dict):
            raise VerificationError(f"Missing [libraries].{alias} in {catalog_path}")

        module = library.get("module")
        version_data = library.get("version")
        version_ref = version_data.get("ref") if isinstance(version_data, dict) else None
        if not isinstance(module, str) or module.count(":") != 1:
            raise VerificationError(f"{alias} must declare module = 'group:name'")
        if not isinstance(version_ref, str) or not isinstance(versions.get(version_ref), str):
            raise VerificationError(f"{alias} must reference a declared string version")

        group, name = module.split(":", maxsplit=1)
        artifacts.append(Artifact(group, name, versions[version_ref]))

    coordinates = {(artifact.group, artifact.version) for artifact in artifacts}
    names = {artifact.name for artifact in artifacts}
    if coordinates != {("dev.rikka.shizuku", artifacts[0].version)} or names != {
        "api",
        "provider",
    }:
        raise VerificationError(
            "Shizuku api and provider must use dev.rikka.shizuku and the same library version"
        )

    return tuple(artifacts)


def verify_artifact(
    artifact: Artifact,
    repository: str,
    *,
    attempts: int,
    timeout: float,
    opener: Callable = urlopen,
    sleeper: Callable[[float], None] = time.sleep,
) -> str:
    url = artifact.pom_url(repository)
    request = Request(
        url,
        headers={"User-Agent": "Language-Selector-CI/1.0"},
        method="GET",
    )

    for attempt in range(1, attempts + 1):
        try:
            with opener(request, timeout=timeout) as response:
                response.read(1)
            return url
        except HTTPError as error:
            if error.code == 404:
                raise VerificationError(
                    f"Missing Maven artifact {artifact.coordinate}: {url}. "
                    "The Shizuku manager-app version is not necessarily the client-library version."
                ) from error
            if error.code not in TRANSIENT_HTTP_STATUSES or attempt == attempts:
                raise VerificationError(
                    f"Cannot verify {artifact.coordinate}: HTTP {error.code} from {url}"
                ) from error
        except (URLError, TimeoutError, OSError) as error:
            if attempt == attempts:
                raise VerificationError(
                    f"Cannot verify {artifact.coordinate} after {attempts} attempts: {error}"
                ) from error

        sleeper(float(2 ** (attempt - 1)))

    raise AssertionError("retry loop exited unexpectedly")


def verify_catalog(
    catalog_path: Path,
    repository: str = DEFAULT_REPOSITORY,
    *,
    attempts: int = 3,
    timeout: float = 15.0,
    opener: Callable = urlopen,
    sleeper: Callable[[float], None] = time.sleep,
) -> tuple[Artifact, ...]:
    if attempts < 1:
        raise VerificationError("attempts must be at least 1")

    artifacts = load_shizuku_artifacts(catalog_path)
    for artifact in artifacts:
        url = verify_artifact(
            artifact,
            repository,
            attempts=attempts,
            timeout=timeout,
            opener=opener,
            sleeper=sleeper,
        )
        print(f"Verified {artifact.coordinate} at {url}")
    return artifacts


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--catalog", type=Path, default=DEFAULT_CATALOG)
    parser.add_argument("--repository", default=DEFAULT_REPOSITORY)
    parser.add_argument("--attempts", type=int, default=3)
    parser.add_argument("--timeout", type=float, default=15.0)
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    try:
        verify_catalog(
            args.catalog,
            args.repository,
            attempts=args.attempts,
            timeout=args.timeout,
        )
    except VerificationError as error:
        print(f"ERROR: {error}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
