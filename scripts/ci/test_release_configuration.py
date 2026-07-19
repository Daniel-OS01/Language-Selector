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
VALIDATE_WORKFLOW = (
    REPOSITORY_ROOT / ".github" / "workflows" / "release-build.yml"
)
PUBLISH_WORKFLOW = REPOSITORY_ROOT / ".github" / "workflows" / "publish-apk.yml"


class ReleaseConfigurationTest(unittest.TestCase):
    def validate_workflow_text(self) -> str:
        return VALIDATE_WORKFLOW.read_text(encoding="utf-8")

    def publish_workflow_text(self) -> str:
        return PUBLISH_WORKFLOW.read_text(encoding="utf-8")

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

        for workflow in (self.validate_workflow_text(), self.publish_workflow_text()):
            self.assertIn("android\\.compileSdk=", workflow)
            self.assertIn('"platforms;android-$compile_sdk"', workflow)
            self.assertNotRegex(workflow, r"platforms;android-\d")

    def test_build_tools_is_single_sourced_and_propagated(self) -> None:
        properties = (REPOSITORY_ROOT / "gradle.properties").read_text(encoding="utf-8")
        matches = re.findall(r"^android\.buildTools=(.+)$", properties, flags=re.MULTILINE)

        self.assertEqual(1, len(matches), "android.buildTools must be declared exactly once")
        self.assertRegex(matches[0], r"^\d+\.\d+\.\d+$")

        validate = self.validate_workflow_text()
        publish = self.publish_workflow_text()

        for workflow in (validate, publish):
            self.assertIn("id: android_versions", workflow)
            self.assertIn("android\\.buildTools=", workflow)
            self.assertIn(
                "build_tools: ${{ steps.android_versions.outputs.build_tools }}", workflow
            )
            self.assertIn('"build-tools;$build_tools"', workflow)
            self.assertNotRegex(workflow, r"build-tools;\d")

        self.assertIn(
            "ANDROID_BUILD_TOOLS: ${{ needs.build.outputs.build_tools }}", publish
        )
        exact_apksigner = (
            'apksigner="$ANDROID_HOME/build-tools/$ANDROID_BUILD_TOOLS/apksigner"'
        )
        self.assertEqual(2, publish.count(exact_apksigner))
        self.assertIn('[[ -x "$apksigner" ]]', publish)
        self.assertNotIn('find "$ANDROID_HOME/build-tools"', publish)
        self.assertNotIn("tail -n 1", publish)

    def test_workflow_paths_cover_all_workflow_files(self) -> None:
        workflow = self.validate_workflow_text()

        self.assertEqual(2, workflow.count("'.github/workflows/**'"))
        self.assertEqual(
            2,
            len(
                re.findall(
                    r"^\s+- '\.github/workflows/\*\*'\s*$", workflow, flags=re.MULTILINE
                )
            ),
        )
        self.assertNotIn("'.github/workflows/release-build.yml'", workflow)

    def test_validation_workflow_does_not_publish(self) -> None:
        workflow = self.validate_workflow_text()

        self.assertNotIn("PUBLISH_REQUESTED:", workflow)
        self.assertNotIn("PUBLISH_EXPLICIT:", workflow)
        self.assertNotIn("bash scripts/ci/check_release_signing.sh", workflow)
        self.assertNotIn("should_publish:", workflow)
        self.assertNotIn("Publish signed APK", workflow)
        self.assertNotIn("gh release create", workflow)
        self.assertIn("testDebugUnitTest lintRelease assembleRelease", workflow)
        # Still ShellChecks the shared signing script without evaluating secrets.
        self.assertIn("shellcheck scripts/ci/check_release_signing.sh", workflow)

    def test_publish_workflow_is_manual_fast_path(self) -> None:
        workflow = self.publish_workflow_text()

        self.assertIn("workflow_dispatch:", workflow)
        self.assertNotIn("\n  push:", workflow)
        self.assertNotIn("\n  pull_request:", workflow)
        self.assertIn("CI_VERSION_CODE=$((GITHUB_RUN_NUMBER + 100000))", workflow)
        self.assertIn("assembleRelease", workflow)
        self.assertNotIn("testDebugUnitTest", workflow)
        self.assertNotIn("lintRelease", workflow)
        self.assertNotIn("actionlint", workflow)
        self.assertNotIn("verify_shizuku_artifacts.py", workflow)
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

    def test_ci_version_code_is_validated_and_wired(self) -> None:
        app_build = (REPOSITORY_ROOT / "app" / "build.gradle.kts").read_text(
            encoding="utf-8"
        )
        validate = self.validate_workflow_text()
        publish = self.publish_workflow_text()

        self.assertIn('System.getenv("CI_VERSION_CODE")', app_build)
        self.assertIn("CI_VERSION_CODE must be an integer from 1 to 2100000000", app_build)
        self.assertIn("?: return 5", app_build)
        self.assertIn("versionCode = resolveVersionCode()", app_build)
        self.assertIn("CI_VERSION_CODE: ${{ github.run_number }}", validate)
        self.assertIn("CI_VERSION_CODE=$((GITHUB_RUN_NUMBER + 100000))", publish)

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

    def test_explicit_publish_with_complete_credentials_enables_publication(self) -> None:
        result, output, summary = self.run_signing_policy(
            explicit=True,
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
