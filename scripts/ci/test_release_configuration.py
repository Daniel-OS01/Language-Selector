from __future__ import annotations

import re
import unittest
from pathlib import Path


REPOSITORY_ROOT = Path(__file__).resolve().parents[2]


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

        self.assertGreaterEqual(workflow.count("github.ref == 'refs/heads/main'"), 2)
        self.assertIn('echo "tag=sha-$GITHUB_SHA"', workflow)
        self.assertNotIn('echo "tag=sha-$short_sha"', workflow)


if __name__ == "__main__":
    unittest.main()
