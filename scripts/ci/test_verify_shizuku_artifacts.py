from __future__ import annotations

import tempfile
import unittest
from pathlib import Path
from unittest.mock import Mock
from urllib.error import HTTPError, URLError

from verify_shizuku_artifacts import VerificationError, verify_catalog


VALID_CATALOG = """
[versions]
shizuku = "13.1.5"

[libraries]
shizuku-api = { module = "dev.rikka.shizuku:api", version.ref = "shizuku" }
shizuku-provider = { module = "dev.rikka.shizuku:provider", version.ref = "shizuku" }
"""


class FakeResponse:
    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_value, traceback):
        return False

    def read(self, _size: int) -> bytes:
        return b"<"


class VerifyShizukuArtifactsTest(unittest.TestCase):
    def setUp(self) -> None:
        self.temp_dir = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp_dir.cleanup)
        self.catalog = Path(self.temp_dir.name) / "libs.versions.toml"
        self.catalog.write_text(VALID_CATALOG, encoding="utf-8")

    def test_verifies_api_and_provider_urls(self) -> None:
        opener = Mock(side_effect=[FakeResponse(), FakeResponse()])

        artifacts = verify_catalog(
            self.catalog,
            "https://repo.example/maven2",
            attempts=1,
            opener=opener,
        )

        self.assertEqual(
            ["dev.rikka.shizuku:api:13.1.5", "dev.rikka.shizuku:provider:13.1.5"],
            [artifact.coordinate for artifact in artifacts],
        )
        requested_urls = [call.args[0].full_url for call in opener.call_args_list]
        self.assertEqual(
            [
                "https://repo.example/maven2/dev/rikka/shizuku/api/13.1.5/api-13.1.5.pom",
                "https://repo.example/maven2/dev/rikka/shizuku/provider/13.1.5/provider-13.1.5.pom",
            ],
            requested_urls,
        )

    def test_reports_missing_artifact_and_version_distinction(self) -> None:
        missing_url = (
            "https://repo.example/maven2/dev/rikka/shizuku/api/13.1.5/api-13.1.5.pom"
        )
        opener = Mock(
            side_effect=HTTPError(missing_url, 404, "Not Found", hdrs=None, fp=None)
        )

        with self.assertRaisesRegex(
            VerificationError,
            "manager-app version is not necessarily the client-library version",
        ):
            verify_catalog(
                self.catalog,
                "https://repo.example/maven2",
                attempts=3,
                opener=opener,
            )

        self.assertEqual(1, opener.call_count)

    def test_retries_transient_network_failure(self) -> None:
        opener = Mock(
            side_effect=[URLError("temporary DNS failure"), FakeResponse(), FakeResponse()]
        )
        sleeper = Mock()

        verify_catalog(
            self.catalog,
            "https://repo.example/maven2",
            attempts=2,
            opener=opener,
            sleeper=sleeper,
        )

        self.assertEqual(3, opener.call_count)
        sleeper.assert_called_once_with(1.0)

    def test_rejects_missing_provider_alias(self) -> None:
        self.catalog.write_text(
            VALID_CATALOG.replace(
                'shizuku-provider = { module = "dev.rikka.shizuku:provider", '
                'version.ref = "shizuku" }\n',
                "",
            ),
            encoding="utf-8",
        )

        with self.assertRaisesRegex(VerificationError, "Missing.*shizuku-provider"):
            verify_catalog(self.catalog, attempts=1, opener=Mock())

    def test_rejects_mismatched_api_and_provider_versions(self) -> None:
        self.catalog.write_text(
            VALID_CATALOG.replace(
                'shizuku = "13.1.5"',
                'shizuku = "13.1.5"\nprovider = "13.1.4"',
            ).replace(
                'shizuku-provider = { module = "dev.rikka.shizuku:provider", '
                'version.ref = "shizuku" }',
                'shizuku-provider = { module = "dev.rikka.shizuku:provider", '
                'version.ref = "provider" }',
            ),
            encoding="utf-8",
        )

        with self.assertRaisesRegex(VerificationError, "same library version"):
            verify_catalog(self.catalog, attempts=1, opener=Mock())


if __name__ == "__main__":
    unittest.main()
