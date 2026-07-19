# Runbook — Publish a signed APK

Operator checklist for **Language Selector Neo**. Portable ideas: [../playbooks/01-release-signing-ci.md](../playbooks/01-release-signing-ci.md), [../playbooks/02-semver-android-versioning.md](../playbooks/02-semver-android-versioning.md).

## Prerequisites

- [ ] Branch `main` is green on **Validate Android Build** for your commit
- [ ] All four secrets exist on the GitHub repo:

| Secret |
| --- |
| `RELEASE_KEYSTORE_BASE64` |
| `RELEASE_KEYSTORE_PASSWORD` |
| `RELEASE_KEY_ALIAS` |
| `RELEASE_KEY_PASSWORD` |

- [ ] You have an offline backup of the keystore and recorded cert SHA-256

## Provision secrets (once)

1. Create or reuse a persistent JKS for `vegabobo.languageselector` (**not** on an Actions runner).
2. Base64-encode the keystore file.
3. Set each secret with `gh secret set NAME` via **stdin** (avoid shell history).
4. Confirm secrets list: `gh secret list`

## Publish: automatic minor (path-filtered push)

1. Merge/push changes under watched paths (`app/**`, Gradle, workflows, `scripts/ci/**`, …).
2. Workflow **Publish APK** runs with **minor** bump.
3. If secrets missing → build may succeed but **publication soft-skips** (check job summary).
4. If secrets present → GitHub Release `vX.Y.Z` marked latest.

## Publish: manual minor or major

1. Actions → **Publish APK** → Run workflow on **`main`**.
2. Inputs:
   - `publish`: `true`
   - `bump`: `minor` (default) or `major`
3. Wait for build + publish jobs.
4. Confirm Release title/tag `vX.Y.Z`, asset `language-selector-vX.Y.Z-<shortsha>.apk`.

## Verify the APK

Use the exact asset name from the Release (or publish job outputs) and the pinned Build Tools `apksigner` (`ANDROID_BUILD_TOOLS` must match `gradle.properties` / the publish job):

```bash
"$ANDROID_HOME/build-tools/$ANDROID_BUILD_TOOLS/apksigner" verify --verbose --print-certs \
  "language-selector-v${VERSION_NAME}-${SHORT_SHA}.apk"
```

Compare certificate SHA-256 to your recorded production fingerprint.

## Install guidance for users

- Install from **GitHub Releases** only.
- Do **not** install unsigned Actions validation artifacts.
- If updating from historical debug-signed `sha-0832269` / `sha-95bc301`, uninstall first.

## Rerun behavior

- Same commit already tagged `vX.Y.Z` → resolver **reuses** version; release upload is idempotent if the asset exists.
- Do not manually delete a published release and recreate casually — prefer a new commit / bump.

## Failure cheat sheet

| Symptom | Action |
| --- | --- |
| Missing secrets (manual) | Add secrets; re-run |
| Restricted to main | Switch ref to `main` |
| SemVer component out of range | See playbook 02; major bump if minors exhausted |
| lintRelease / tests failed | Fix on a validate workflow first; publish is a fast path without re-lint |
