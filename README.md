# Language Selector Neo

[Validate Android Build](https://github.com/Daniel-OS01/Language-Selector/actions/workflows/release-build.yml)
[Publish APK](https://github.com/Daniel-OS01/Language-Selector/actions/workflows/publish-apk.yml)
CodeRabbit Pull Request Reviews

**Per-app languages for Android 13+**, when the system Settings UI does not expose them. 
Fork with working pins, presets, backup, and stability fixes.

---

## ✨ What's new in this fork


| Area                 | What you get                                                                                       |
| -------------------- | -------------------------------------------------------------------------------------------------- |
| 📌 Pins & favorites  | Long-press a language to pin it; pins stay at the top of the list and feed the Quick Settings tile |
| 💾 Presets           | Save / apply / delete named snapshots of all currently modified apps (up to 10)                    |
| 📤 Export & import   | JSON backup of modified apps, pins, and presets via the system document picker                     |
| 🔎 Search & filters  | Faster loading, Enter-to-search, history, system / modified filters, clearer empty states          |
| 🏷️ Labels & sorting | `System` / `User` / `Modified` labels; modified apps rise to the top with snackbar navigation      |
| 🐛 Stability         | Startup shell crash fixed; pinning from All languages works; list refresh no longer races          |
| 🚀 Releases          | Signed SemVer tags (`vX.Y.Z`), not opaque `sha-*` titles; CI-tested and lint-clean                 |


Upstream core idea is unchanged: Shizuku (or root) talks to Android's per-app locale APIs. This fork focuses on **features that work** and **bugs that stay fixed**.

---



## 📋 Requirements


| Need     | Detail                                                                                                               |
| -------- | -------------------------------------------------------------------------------------------------------------------- |
| Android  | 13 or newer (`minSdk 33`)                                                                                            |
| Shizuku  | Running manager + permission for this app                                                                            |
| Managers | [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku) or [thedjchi/Shizuku](https://github.com/thedjchi/Shizuku) |


> **Note:** Manager APK version ≠ Maven library version. This project uses `dev.rikka.shizuku:api` / `provider` **13.1.5** (matches the API embedded in `thedjchi/Shizuku` v13.7.0).

---



## 📦 Download

Get signed APKs from **[GitHub Releases](https://github.com/Daniel-OS01/Language-Selector/releases)**.


| Artifact               | Safe to install?                  |
| ---------------------- | --------------------------------- |
| GitHub Release APK     | Yes (production signing)          |
| Actions validation APK | No — unsigned / for CI proof only |


Older `sha-0832269` / `sha-95bc301` builds used different debug certs — uninstall those before installing a stable-signed release.

---



## 🚀 Quick start

1. Install and start Shizuku.
2. Install Language Selector Neo from Releases.
3. Open the app, grant Shizuku, tap **Proceed**.
4. Pick an app → pick a language.
5. Optional: overflow menu → **Settings** → save a **preset** or **Export** / **Import** a backup.

The target app must already ship translations for that locale. This app does not translate; it only sets the per-app locale Android should use.

Avoid changing locales on unsupported or critical system apps.

---



## 🧩 Features



### 🌐 Per-app languages

Set locales for user apps (and system apps when you explicitly enable them) through Shizuku's privileged locale service.

### 📌 Pins & favorites

Long-press a language to pin or unpin. Pinned languages stay at the top of the language list and power the Quick Settings tile.

### 💾 Presets

In **Settings**, snapshot every currently modified package → language tag under a name. **Apply** restores that set and clears modified apps not in the preset. Shizuku must be connected. Max **10** presets.

### 📤 Export & import

Also in **Settings**:


| Action | Behavior                                                                        |
| ------ | ------------------------------------------------------------------------------- |
| Export | Writes JSON (`schemaVersion` 1): apps, pins, presets                            |
| Import | Applies app locales like a full restore; replaces pins and presets when present |


Uses the system Create / Open Document pickers; short success/error feedback afterward.

### 🔎 Search & filters

Search installed apps, reuse history, toggle system apps, focus on modified apps.

### ⚡ Quick Settings tile

Change the foreground app's language from the shade using your pins. Unavailable until at least one language is pinned. System apps are not changed from the tile.

### 🗂️ Language list

Built from `Locale.getAvailableLocales()` — many entries may not be supported by a given app.

---



## 🖼️ Screenshots



---



## 🏷️ Versioning


| Trigger                         | Bump                   | Tag example         |
| ------------------------------- | ---------------------- | ------------------- |
| Push to `main` (app / CI paths) | Minor                  | `v2.1.0` → `v2.2.0` |
| Manual Publish (`bump=minor`)   | Minor                  | next `vX.(Y+1).0`   |
| Manual Publish (`bump=major`)   | Major (only this path) | next `v(X+1).0.0`   |


- Latest `v*` tag wins; historical `sha-*` tags are ignored.
- Same commit rerun reuses an existing SemVer tag (no double-bump).
- `versionCode = major×1_000_000 + minor×1_000 + patch`
- Local default: `versionName` `2.0.0`, `versionCode` `5` without CI env vars.
- First SemVer publish with no `v*` tags yet: `v2.1.0` (fallback last version `2.0.0` + minor).

---



## 🛠️ Building from source


| Tool                 | Version         |
| -------------------- | --------------- |
| JDK                  | 21              |
| Android SDK Platform | 37.0            |
| Build Tools          | 36.0.0          |
| Wrapper              | included Gradle |


`android.compileSdk` and `android.buildTools` are single-sourced in `gradle.properties`.

```bash
./gradlew --no-daemon --stacktrace testDebugUnitTest lintRelease assembleRelease
```

Without release-signing env vars you get:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

CI script checks:

```bash
python3 -m unittest discover -s scripts/ci -p 'test_*.py'
python3 scripts/ci/verify_shizuku_artifacts.py
```

---



## 🤖 CI and release behavior


| Workflow                                         | Role                                                                                                                                    |
| ------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------- |
| **Validate Android Build** (`release-build.yml`) | PR / `main`+`work` path pushes: unit tests, lint, actionlint, Shizuku check, unsigned `assembleRelease`. No secrets, no GitHub Release. |
| **Publish APK** (`publish-apk.yml`)              | Path-filtered `main` push (minor) or manual dispatch (minor/major): signed APK, SemVer tag, draft-then-publish.                         |




### 🔐 Signing secrets


| Secret                      |
| --------------------------- |
| `RELEASE_KEYSTORE_BASE64`   |
| `RELEASE_KEYSTORE_PASSWORD` |
| `RELEASE_KEY_ALIAS`         |
| `RELEASE_KEY_PASSWORD`      |


1. Use one persistent production keystore (never generate it on an Actions runner).
2. Back it up offline; store passwords/alias in a password manager.
3. Record the cert SHA-256 for later APK comparison.
4. `gh secret set` each value via stdin.
5. Push a path-filtered change to `main`, or run **Publish APK** with `publish=true` and `bump=minor` or `major`.

Push without secrets soft-skips publication; explicit manual publish fails if any secret is missing.

---



## ℹ️ Background

Android 13 added per-app language preferences, but some OEMs hide them in Settings even when the locale service exists. Language Selector Neo uses Shizuku to drive those APIs without the stock UI.

---



## 🤝 Contributing

Project documentation (changelog, troubleshooting, and reusable Android playbooks) lives in `[docs/](docs/README.md)`.

Run unit tests, Android lint, release assembly, and relevant `scripts/ci` checks before opening a PR.

JVM coverage includes pinning / locale list (`LocaleManagerTest`) and backup JSON (`LocaleBackupCodecTest`). Compose UI instrumentation is not set up here.

Do not commit build outputs, unsigned APKs, keystores, or credentials.

---



## 📜 Attribution

Based on [VegaBobo/Language-Selector](https://github.com/VegaBobo/Language-Selector). See [LICENSE](LICENSE).
Fork of [ezn24/Language-Selector-Neo](https://github.com/ezn24/Language-Selector-Neo).