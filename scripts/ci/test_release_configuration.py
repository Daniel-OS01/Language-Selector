from __future__ import annotations

import os
import re
import subprocess
import tempfile
import unittest
from pathlib import Path


REPOSITORY_ROOT = Path(__file__).resolve().parents[2]
SIGNING_SCRIPT = REPOSITORY_ROOT / "scripts" / "ci" / "check_release_signing.sh"
SIGNING_SECRET_NAMES = (
    "RELEASE_KEYSTORE_BASE64",
    "RELEASE_KEYSTORE_PASSWORD",
    "RELEASE_KEY_ALIAS",
    "RELEASE_KEY_PASSWORD",
)


class ReleaseConfigurationTest(unittest.TestCase):
    def test_compile_sdk_has_one_value_used_by_modules_and_ci(self) -> None:
        properties = (REPOSITORY_ROOT / "gradle.properties").read_text(encoding="utf-8")
        matches = re.findall(r"^android\.compileSdk=(.+)$", properties, flags=re.MULTILINE)

        self.assertEqual(1, len(matches), "android.compileSdk must be declared exactly once")
        self.assertRegex(matches[0], r"^\d+\.\d+$")

        for module in ("app", "hidden_api"):
            build_file = (REPOSITORY_ROOT / module / "build.gradle.kts").read_text(
                encoding="utf-8"
            )
            self.assertIn('providers.gradleProperty("android.compileSdk")', build_file)
            self.assertNotRegex(build_file, r"compileSdk\s*=\s*\d+")

        workflow = (
            REPOSITORY_ROOT / ".github" / "workflows" / "release-build.yml"
        ).read_text(encoding="utf-8")
        self.assertIn("android\\.compileSdk=", workflow)
        self.assertIn('"platforms;android-$compile_sdk"', workflow)
        self.assertNotRegex(workflow, r"platforms;android-\d")

    def test_publish_is_main_only_and_uses_a_full_sha_tag(self) -> None:
        workflow = (
            REPOSITORY_ROOT / ".github" / "workflows" / "release-build.yml"
        ).read_text(encoding="utf-8")

        self.assertIn("PUBLISH_REQUESTED:", workflow)
        self.assertIn("PUBLISH_EXPLICIT:", workflow)
        self.assertIn(
            "should_publish: ${{ steps.signing.outputs.should_publish }}", workflow
        )
        self.assertIn(
            "if: env.PUBLISH_EXPLICIT == 'true' && github.ref != 'refs/heads/main'",
            workflow,
        )
        self.assertIn(
            "if: env.PUBLISH_REQUESTED == 'true' && github.ref == 'refs/heads/main'",
            workflow,
        )
        self.assertIn("needs.build.outputs.should_publish == 'true'", workflow)
        self.assertIn('echo "tag=sha-$GITHUB_SHA"', workflow)
        self.assertNotIn('echo "tag=sha-$short_sha"', workflow)

    def run_signing_policy(
        self,
        *,
        explicit: bool,
        ref: str = "refs/heads/main",
        secrets: dict[str, str] | None = None,
    ) -> tuple[subprocess.CompletedProcess[str], str, str]:
        with tempfile.TemporaryDirectory() as temp_dir:
            output_path = Path(temp_dir) / "github-output"
            summary_path = Path(temp_dir) / "step-summary"
            environment = os.environ.copy()
            for name in SIGNING_SECRET_NAMES:
                environment.pop(name, None)
            environment.update(
                {
                    "PUBLISH_EXPLICIT": str(explicit).lower(),
                    "GITHUB_REF": ref,
                    "GITHUB_OUTPUT": str(output_path),
                    "GITHUB_STEP_SUMMARY": str(summary_path),
                }
            )
            environment.update(secrets or {})

            result = subprocess.run(
                ["bash", str(SIGNING_SCRIPT)],
                check=False,
                capture_output=True,
                env=environment,
                text=True,
            )
            output = output_path.read_text(encoding="utf-8") if output_path.exists() else ""
            summary = (
                summary_path.read_text(encoding="utf-8") if summary_path.exists() else ""
            )
            return result, output, summary

    def test_automatic_main_build_continues_without_signing_secrets(self) -> None:
        result, output, summary = self.run_signing_policy(explicit=False)

        self.assertEqual(0, result.returncode)
        self.assertEqual("should_publish=false\n", output)
        self.assertIn("Release publication skipped", result.stdout)
        self.assertIn("RELEASE_KEYSTORE_BASE64", summary)
        self.assertIn("will not create a GitHub Release", summary)

    def test_automatic_main_build_reports_exact_partial_secret_set(self) -> None:
        result, output, _summary = self.run_signing_policy(
            explicit=False,
            secrets={"RELEASE_KEYSTORE_BASE64": "encoded-keystore"},
        )

        self.assertEqual(0, result.returncode)
        self.assertEqual("should_publish=false\n", output)
        self.assertIn(
            "Missing GitHub Actions secrets: RELEASE_KEYSTORE_PASSWORD "
            "RELEASE_KEY_ALIAS RELEASE_KEY_PASSWORD",
            result.stdout,
        )

    def test_explicit_publish_fails_without_signing_secrets(self) -> None:
        result, output, _summary = self.run_signing_policy(explicit=True)

        self.assertNotEqual(0, result.returncode)
        self.assertEqual("", output)
        self.assertIn("Missing GitHub Actions secrets", result.stderr)

    def test_explicit_publish_fails_outside_main(self) -> None:
        result, output, _summary = self.run_signing_policy(
            explicit=True,
            ref="refs/heads/work",
            secrets={name: "configured" for name in SIGNING_SECRET_NAMES},
        )

        self.assertNotEqual(0, result.returncode)
        self.assertEqual("", output)
        self.assertIn("restricted to refs/heads/main", result.stderr)

    def test_complete_signing_configuration_enables_publication(self) -> None:
        result, output, summary = self.run_signing_policy(
            explicit=False,
            secrets={name: "configured" for name in SIGNING_SECRET_NAMES},
        )

        self.assertEqual(0, result.returncode)
        self.assertEqual("should_publish=true\n", output)
        self.assertEqual("", summary)

    def test_dependabot_loop_reads_urls_without_backslash_processing(self) -> None:
        workflow = (
            REPOSITORY_ROOT / ".github" / "workflows" / "dependabot-auto-merge.yml"
        ).read_text(encoding="utf-8")

        self.assertIn("while IFS= read -r pr; do", workflow)
        self.assertNotRegex(workflow, r"while\s+read\s+pr;")


if __name__ == "__main__":
    unittest.main()
