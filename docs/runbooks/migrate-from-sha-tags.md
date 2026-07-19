# Runbook — Migrate from `sha-*` tags to SemVer

How Language Selector Neo moved from opaque SHA releases to `vMAJOR.MINOR.PATCH`, and how to apply the same idea elsewhere.

## What changed

| Before | After |
| --- | --- |
| Tag `sha-<full-commit-sha>` | Tag `vX.Y.Z` |
| Title `Build sha-<short>` | Title `vX.Y.Z` |
| Fixed `versionName` (e.g. `2.0.0`) | `CI_VERSION_NAME` from resolver |
| `versionCode` ≈ `run_number + offset` | Encoded from SemVer |

Historical `sha-*` releases **remain** on GitHub. They are **ignored** when computing the next version.

## Bootstrap rules (Neo)

1. If **no** `v*` tags exist, treat last released as **`2.0.0`** (matches prior About default).
2. First SemVer publish → **`v2.1.0`** (minor bump).
3. Optional (not required): create an annotated `v2.0.0` on an older commit for clarity — resolver does not need it.

## Certificate / install migration

Some early SHA releases used **different Android Debug certificates**:

- `sha-0832269`
- `sha-95bc301`

Those **cannot** update in place to each other or to the persistent release keystore builds.

**User action:** uninstall the old app → install the latest signed SemVer release from GitHub Releases.

## Operator checklist when cutting over in another app

- [ ] Stop creating SHA tags in the publish workflow
- [ ] Add a resolver that only considers `v*` SemVer tags
- [ ] Document that old tags are historical / ignored
- [ ] Ensure new `versionCode` is **greater** than any previously installed production code (SemVer encoding at `2.1.0` → `2001000` clears Neo’s old `~1000xx` codes)
- [ ] Keep one signing identity going forward

## Related

- [../playbooks/02-semver-android-versioning.md](../playbooks/02-semver-android-versioning.md)
- [publish-signed-apk.md](publish-signed-apk.md)
- [../neo/TROUBLESHOOTING.md](../neo/TROUBLESHOOTING.md)
