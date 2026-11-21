# Language Selector Neo — troubleshooting

Symptom → cause → fix. Prefer installing APKs from **GitHub Releases**, not Actions artifacts.

For portable patterns, see [../playbooks/](../playbooks/).

---

## Install and signing

### `INSTALL_PARSE_FAILED_NO_CERTIFICATES`

| | |
| --- | --- |
| **Symptom** | Install fails; package appears unsigned |
| **Cause** | Installing an **unsigned** Actions validation APK |
| **Fix** | Install only from [GitHub Releases](https://github.com/Daniel-OS01/Language-Selector/releases). Validate workflow intentionally uploads unsigned APKs |

### Cannot update over older `sha-0832269` / `sha-95bc301`

| | |
| --- | --- |
| **Symptom** | Update install rejected / signature mismatch |
| **Cause** | Those releases used ephemeral Android Debug certificates, not the persistent release keystore |
| **Fix** | Uninstall the old build, then install the current signed release. See [../runbooks/migrate-from-sha-tags.md](../runbooks/migrate-from-sha-tags.md) |

### Publish job soft-skips or fails on secrets

| | |
| --- | --- |
| **Symptom** | Log: missing `RELEASE_KEYSTORE_*` / `RELEASE_KEY_*`; `should_publish=false` or hard fail |
| **Cause** | Incomplete signing secrets. Automatic push soft-skips; manual `publish=true` fails |
| **Fix** | Configure all four secrets; see [../runbooks/publish-signed-apk.md](../runbooks/publish-signed-apk.md) and [../playbooks/01-release-signing-ci.md](../playbooks/01-release-signing-ci.md) |

### Manual publish outside `main`

| | |
| --- | --- |
| **Symptom** | “Manual publication is restricted to refs/heads/main” |
| **Cause** | Policy in `check_release_signing.sh` / workflow gate |
| **Fix** | Run Publish APK on `main` only |

---

## Versioning and tags

### About still shows `v2.0.0` after many commits

| | |
| --- | --- |
| **Symptom** | Local or validation build shows default `2.0.0` |
| **Cause** | Local/validate builds use default `versionName` / `run_number` versionCode unless `CI_VERSION_NAME` / SemVer resolver ran |
| **Fix** | Only **Publish APK** path sets SemVer via `resolve_next_version.sh`. Look for `v*` tags on Releases |

### Releases still titled `Build sha-…`

| | |
| --- | --- |
| **Symptom** | Old releases use SHA tags |
| **Cause** | Historical publisher; new flow uses `vX.Y.Z` |
| **Fix** | New publishes create SemVer tags. Old `sha-*` remain historical and are **ignored** by the bump resolver |

### Publish fails: `SemVer component out of range (>=1000)`

| | |
| --- | --- |
| **Symptom** | `resolve_next_version.sh` exits non-zero |
| **Cause** | A SemVer component would encode with ≥1000 (or over-long digit string) under `major*1e6+minor*1e3+patch` |
| **Fix** | Do not create tags with huge components; cut a **major** bump if minor is approaching 999. See [../playbooks/02-semver-android-versioning.md](../playbooks/02-semver-android-versioning.md) |

---

## Lint / CI

### `StringFormatMatches` on `version`

| | |
| --- | --- |
| **Symptom** | Lint: `%2$s` vs `%2$d` conflict across `values*` |
| **Cause** | English uses `%2$d` for `VERSION_CODE` (int); some locales still had `%2$s` |
| **Fix** | Align all locale `version` strings to `%2$d`. About uses `BuildConfig.VERSION_CODE` |

### `MissingTranslation` fails `lintRelease`

| | |
| --- | --- |
| **Symptom** | New keys (e.g. Settings / presets) fail lint with 15+ errors |
| **Cause** | This project treats MissingTranslation as **error**, not warning |
| **Fix** | Add translations in `values-ja`, `values-pt-rBR`, `values-zh-rCN`, `values-zh-rTW` (or adjust lint severity — not done here) |

### Unit test: `JSONObject.put` / “not mocked”

| | |
| --- | --- |
| **Symptom** | `LocaleBackupCodecTest` RuntimeException on JVM |
| **Cause** | Unit tests use Android stub `org.json` |
| **Fix** | `testImplementation("org.json:json:…")` in `app/build.gradle.kts`. See [../playbooks/08-unit-testing-android-jvm.md](../playbooks/08-unit-testing-android-jvm.md) |

---

## Runtime / Shizuku

### “Language service unavailable”

| | |
| --- | --- |
| **Symptom** | Settings save/apply/import fails with service message |
| **Cause** | Shizuku not running / not granted, or root path not connected |
| **Fix** | Start manager, grant permission, tap Proceed on main; retry |

### Preset apply / import partially wrong

| | |
| --- | --- |
| **Symptom** | Some apps keep old locales |
| **Cause** | Per-package `setApplicationLocales` failures are isolated (continue on error); or package uninstalled |
| **Fix** | Re-apply; check logcat for IPC failures; ensure packages still installed |

### Settings crashes on open (malformed presets)

| | |
| --- | --- |
| **Symptom** | Historical: crash loading presets |
| **Cause** | Bad JSON in `locale_presets` |
| **Fix** | Codec/`reloadPresets` now return empty on failure. Clear app data if prefs still corrupt |

### App list flickers / wrong contents after refresh

| | |
| --- | --- |
| **Symptom** | Historical race after import + filter toggle |
| **Cause** | Overlapping `fillListOfApps` coroutines mutating the same list |
| **Fix** | `fillListJob` cancel-and-replace in `MainScreenVm` |

### Crash at startup related to libsu Shell

| | |
| --- | --- |
| **Symptom** | Crash early in process lifetime |
| **Cause** | `Shell.setDefaultBuilder` in Activity instance initializer (too early / wrong lifecycle) |
| **Fix** | Configure Shell in `App.onCreate()` |

---

## Features behavior (not bugs)

| Expectation | Actual design |
| --- | --- |
| Preset saves “all apps” | Only **modified** apps (non-empty application locales) |
| Apply leaves other modified apps alone | Apply **clears** modified apps not in the preset |
| Export encrypts backup | Plain JSON via SAF; no encryption |
| Tile works with zero pins | Tile unavailable until ≥1 pinned language |

---

## Validation commands

```bash
python3 -m unittest discover -s scripts/ci -p 'test_*.py'
./gradlew --no-daemon testDebugUnitTest lintRelease
```

On CI, also rely on actionlint / ShellCheck for workflow and `scripts/ci/*.sh`.
