# Language Selector Neo

## CodeRabbit Pull Request Reviews

![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/Daniel-OS01/Language-Selector?utm_source=oss&utm_medium=github&utm_campaign=Daniel-OS01%2FLanguage-Selector&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

[![Build and Publish APK on Updates](https://github.com/Daniel-OS01/Language-Selector/actions/workflows/release-build.yml/badge.svg)](https://github.com/Daniel-OS01/Language-Selector/actions/workflows/release-build.yml)

Language Selector Neo is a fork of [VegaBobo/Language-Selector](https://github.com/VegaBobo/Language-Selector). It provides a front end for Android's per-app language APIs on devices where the system settings do not expose them.

This fork is developed with Codex-assisted "vibe coding" and validated with automated tests, Android lint, release builds, and CI workflow checks.

## What's changed in this fork

- Faster app loading and a more responsive search experience.
- Search is submitted with Enter instead of inserting a new line.
- Search history, empty-state messages, and system/modified filters.
- App labels for `System`, `User`, and `Modified` applications.
- Modified applications are moved toward the top of the list with navigation feedback.
- Favorite languages can be pinned by long-pressing them.
- Pinned languages appear at the top of the language list and in the Quick Settings tile.
- Traditional Chinese support, with updated Japanese and Brazilian Portuguese resources.
- Shizuku dependency resolution through Maven Central.
- Android SDK 37.0 and Java 21 build support.
- Hardened GitHub Actions validation, signing, artifact, and release handling.

## Requirements

- Android 13 or newer (`minSdk 33`).
- A running Shizuku manager with permission granted to Language Selector Neo.

You can use either:

- [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku), the original project.
- [thedjchi/Shizuku](https://github.com/thedjchi/Shizuku), the fork used by this repository's maintainer.

The installed manager version and the Maven client-library version are different version lines. This project uses `dev.rikka.shizuku:api` and `dev.rikka.shizuku:provider` version `13.1.5`, which matches the API embedded by `thedjchi/Shizuku` v13.7.0.

## Download

Published APKs are available from [GitHub Releases](https://github.com/Daniel-OS01/Language-Selector/releases).

GitHub Actions may also contain unsigned validation APKs. Those artifacts prove that the project builds, but they are not published releases and are not intended for installation or distribution.

## Usage

1. Install and start your preferred Shizuku manager.
2. Start Shizuku using its supported method for your device.
3. Install Language Selector Neo from GitHub Releases.
4. Open the app, grant the Shizuku permission, and tap **Proceed**.
5. Select an application.
6. Select the language that application should use.

The selected application must include translations for that locale. Language Selector Neo does not translate applications; it only tells Android which supported locale the application should use.

Changing the locale of unsupported or system applications can cause unexpected behavior and is not recommended.

## Features

### Per-app languages

Set the locale for user applications and, when explicitly enabled, system applications. The app uses Android's locale-management service through Shizuku so it can manage other packages.

### Search and filters

Search the installed-app list, reuse recent searches, show or hide system applications, and focus on applications whose locale has already been modified.

### Pinned languages

Long-press a language to pin or unpin it. Pinned languages appear at the top of the language list and are available from the Quick Settings tile.

### Quick Settings tile

Add the Language Selector tile to change the foreground application's language quickly. The tile uses pinned languages and is unavailable until at least one language is pinned. Changing system-app languages from the tile is not supported.

### Language availability

The app builds its language list from `java.util.Locale.getAvailableLocales()`. This exposes many locales, including entries that individual applications may not support.

## Screenshots

<div>
  <img src="https://raw.githubusercontent.com/VegaBobo/Language-Selector/main/other/preview_1.jpg" alt="Application list" width="200" />
  <img src="https://raw.githubusercontent.com/VegaBobo/Language-Selector/main/other/preview_2.jpg" alt="Language selection" width="200" />
</div>

## Building from source

### Toolchain

- JDK 21.
- Android SDK Platform 37.0.
- Android SDK Build Tools 36.0.0.
- The included Gradle wrapper.

`android.compileSdk=37.0` and `android.buildTools=36.0.0` are single-sourced in `gradle.properties` and consumed by both Android modules and CI. CI installs those exact packages and verifies signed APKs with `$ANDROID_HOME/build-tools/<buildTools>/apksigner`.

Local builds without `CI_VERSION_CODE` keep `versionCode=5`. CI sets `CI_VERSION_CODE` from `github.run_number`, so publishable APKs get a positive, monotonic version code that stays stable across reruns of the same workflow run.

Run the same validation sequence used by GitHub Actions:

```bash
./gradlew --no-daemon --stacktrace testDebugUnitTest lintRelease assembleRelease
```

Without release-signing environment variables, Gradle intentionally creates:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

You can also run the CI regression checks directly:

```bash
python3 -m unittest discover -s scripts/ci -p 'test_*.py'
python3 scripts/ci/verify_shizuku_artifacts.py
```

The Shizuku verifier reads the version catalog, checks both configured coordinates, retries transient network failures, and fails immediately for a missing Maven artifact.

## CI and release behavior

The `Build and Publish APK on Updates` workflow validates relevant changes to the Android modules, Gradle configuration, CI scripts, and any file under `.github/workflows/**`. README-only changes do not start this workflow.

- Pull requests and `work` pushes run tests, lint, and a release assembly without publishing.
- Automatic `main` pushes publish only when all signing secrets are configured.
- If signing secrets are missing from an automatic `main` push, the workflow continues as an unsigned validation build, uploads an Actions artifact, emits a warning, and skips the GitHub Release.
- A manual run with `publish=false` is validation-only and does not read signing secrets.
- A manual run with `publish=true` is restricted to `main` and fails clearly if any signing secret is missing.
- Publishable APKs are verified with the configured Build Tools `apksigner` before upload and again before release publication.
- Releases use full-commit-SHA tags and a draft-first, rerun-safe publishing flow.

### Release signing runbook

Signed publication requires these repository Actions secrets:

- `RELEASE_KEYSTORE_BASE64`
- `RELEASE_KEYSTORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`

Provision them once outside GitHub Actions:

1. Reuse the intended production keystore for `vegabobo.languageselector`, or generate one persistent RSA-4096 JKS keystore locally. Never generate the production key on an Actions runner.
2. Keep an encrypted offline backup of the keystore and store both passwords plus the alias in a password manager.
3. Record the public certificate SHA-256 fingerprint for later comparison with published APKs.
4. Base64-encode the keystore and configure each secret with `gh secret set` via stdin so values never appear in shell history or command arguments.
5. After all four secrets exist, automatic `main` pushes and manual `publish=true` runs on `main` publish a verified signed APK.

Unsigned or debug-signed APKs are never published as GitHub Releases by the current workflow.

### Historical certificate migration

The older Releases `sha-0832269` and `sha-95bc301` were signed with different ephemeral Android Debug certificates. Neither can update the other in place, and neither can update the first APK signed by the persistent release key. Uninstall those historical builds before installing a stable-signed release.

## Background

Android 13 introduced per-app language preferences, but some Android distributions do not expose the feature in their Settings application even when the underlying locale service exists.

Android's locale manager can be controlled through privileged APIs or ADB. Language Selector Neo uses Shizuku to access those APIs and provide a convenient interface for changing application locales without requiring the stock Settings UI.

## Contributing

Issues and pull requests are welcome. Before submitting a change, run the unit tests, Android lint, release assembly, and any relevant CI-script checks.

Please avoid committing generated build outputs, unsigned APKs, keystores, or signing credentials.

## Attribution

Language Selector Neo is based on [VegaBobo/Language-Selector](https://github.com/VegaBobo/Language-Selector). See [LICENSE](LICENSE) for licensing information.
