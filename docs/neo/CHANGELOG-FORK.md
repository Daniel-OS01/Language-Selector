# Language Selector Neo — fork changelog

Chronological record of major product and infrastructure changes in the **Daniel-OS01/Language-Selector** fork of VegaBobo’s Language Selector. Paths are relative to the repo root.

This is not a SemVer release notes file for every `v*` tag; it documents **themes of work** so you can map features and fixes to code.

---

## Upstream baseline

- **Origin:** [VegaBobo/Language-Selector](https://github.com/VegaBobo/Language-Selector)
- **Purpose:** UI for Android 13+ per-app locales via Shizuku (or root) when OEM Settings hide the feature
- **Package:** `vegabobo.languageselector`

---

## Product features added in this fork

### Search, filters, and list UX

- Faster app list loading with caching of parsed `AppInfo`
- Search submitted with Enter (no newline insertion)
- Search history, empty states, system / modified filters
- Labels: System / User / Modified; modified apps sorted upward with snackbar navigation

**Primary code:** `app/src/main/java/vegabobo/languageselector/ui/screen/main/`

### Pins and favorites

- Long-press a language to pin/unpin
- Pinned languages appear at the top of the language list and feed the Quick Settings tile
- Fixed wiring so “All languages” long-press can pin (seed `LocaleManager` correctly)

**Primary code:** `LocaleManager`, `AppInfoVm` / App Info UI, `QSTile`

### Settings: presets

- Named snapshots of **currently modified** apps (`packageName` → `languageTag`)
- Apply = set preset locales **and** clear modified apps not in the preset
- Max 10 presets in SharedPreferences (`locale_presets` JSON)

**Primary code:** `data/LocaleBackupCodec.kt`, `data/LocaleSnapshot.kt`, `ui/screen/settings/`

### Settings: export / import

- SAF Create Document / Open Document for JSON backups
- Schema version 1: `apps`, `pinnedLocales`, `presets`, `exportedAt`
- Import applies locales like a full restore and replaces pins/presets when present

**Primary code:** same as presets + `SettingsVm` export/import methods

### Locales / i18n resources

- Traditional Chinese (`values-zh-rTW`) plus updates for ja / pt-BR / zh-CN
- New Settings strings translated where lint treats `MissingTranslation` as an error

---

## Stability and correctness fixes

| Issue | Fix | Where |
| --- | --- | --- |
| Startup / shell-related crash | Move `Shell.setDefaultBuilder` from Activity instance `init` to `Application.onCreate` | `App.kt` |
| Pinning unreliable from All languages | Seed locale manager; wire long-press pin | `LocaleManager`, App Info UI |
| Concurrent app-list refresh races | Cancel-and-replace `fillListJob` | `MainScreenVm.kt` |
| Backup JSON crashes Settings | Catch `JSONException`; defensive preset decode; safe `reloadPresets` | `LocaleBackupCodec.kt`, `SettingsVm.kt` |
| One failing `setApplicationLocales` aborted whole apply | Per-package try/catch | `LocaleSnapshot.kt` |
| `runBusy` swallowed coroutine cancellation | Rethrow `CancellationException` | `SettingsVm.kt` |
| About repo pointed at wrong fork | URL → `Daniel-OS01/Language-Selector` | `AboutScreen.kt` |

---

## Build, CI, and release evolution

### Toolchain

- Compile SDK **37.0**, Build Tools **36.0.0** single-sourced in `gradle.properties`
- Java **21**
- Shizuku client libraries from Maven Central (`api`/`provider` **13.1.5**)

### Validate vs publish split

| Workflow | Role |
| --- | --- |
| `release-build.yml` (“Validate Android Build”) | Tests, lint, actionlint, Shizuku artifact check, **unsigned** assemble — no Releases |
| `publish-apk.yml` (“Publish APK”) | Signed assemble + GitHub Release |

### Signing

- Four repository secrets for a **persistent** release keystore
- Exact `$ANDROID_HOME/build-tools/$ANDROID_BUILD_TOOLS/apksigner` verification (build + publish jobs)
- Soft-skip publish on automatic `main` when secrets missing; hard-fail on explicit manual publish

### Versioning (current)

- User-visible **`versionName`** SemVer (`CI_VERSION_NAME`)
- **`versionCode`** = `major*1_000_000 + minor*1_000 + patch`
- Tags: `vMAJOR.MINOR.PATCH` (not `sha-<full-sha>`)
- Push to `main` (path-filtered) → **minor** bump; manual dispatch can choose **major**
- Resolver: [`scripts/ci/resolve_next_version.sh`](../scripts/ci/resolve_next_version.sh)
- Fallback last version without `v*` tags: `2.0.0` → first publish `v2.1.0`

### Lint / test hardening

- Align `version` format args (`%2$d`) across locales
- Translate new Settings keys (MissingTranslation was failing CI as **error**)
- JVM unit tests: real `org.json` test dependency (Android stubs throw “not mocked”)
- Shell/Python CI regression suite under `scripts/ci/`

---

## Out of scope (intentionally not done)

- Cloud sync / encryption of presets
- Compose UI instrumentation tests
- Retagging or deleting historical `sha-*` GitHub Releases
- Auto-patch SemVer channel (patch stays `0`)

---

## See also

- [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
- [../playbooks/](../playbooks/)
- [../runbooks/publish-signed-apk.md](../runbooks/publish-signed-apk.md)
