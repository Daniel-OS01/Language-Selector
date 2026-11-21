# Playbook 03 — GitHub Actions for Android

## Problem / when you need this

One workflow that “builds and maybe releases” becomes:

- PRs publishing by accident
- README-only pushes burning CI minutes
- Non-deterministic cancelation of in-flight publishes
- Draft/release races on reruns

## Recommended architecture

**Two workflows (or two clearly gated jobs):**

| Workflow | Triggers | Permissions | Output |
| --- | --- | --- | --- |
| Validate | `pull_request`, push to working branches, path filters | `contents: read` | Tests + unsigned APK artifact |
| Publish | `push` to release branch (path-filtered) and/or `workflow_dispatch` | Workflow / build job: `contents: read`; publish job: `contents: write` (artifact download uses the job token; add `actions: read` only if your org requires it explicitly) | Signed APK + GitHub Release |

**Path filters (example):** Android modules, Gradle files, `.github/workflows/**`, `scripts/ci/**` — **not** README-only.

**Concurrency:**

- Validate: cancel in-progress on PRs / non-main OK
- Publish: `cancel-in-progress: false` so a release is not killed mid-upload

**Release idempotency:**

1. Create draft release + upload asset  
2. Mark published / latest only after upload succeeds  
3. Rerun: if published release already has the expected asset → success; if published without asset → fail (do not clobber)

## Concrete checklist

- [ ] Path filters on both validate and publish
- [ ] Publish restricted to release branch (`main`)
- [ ] Separate `PUBLISH_REQUESTED` vs `PUBLISH_EXPLICIT` (auto soft-skip vs manual fail)
- [ ] Pin third-party Actions by commit SHA
- [ ] actionlint + ShellCheck on workflow / CI scripts in validate
- [ ] Timeouts on jobs

## Pitfalls we hit + fixes (Neo)

| Pitfall | Fix |
| --- | --- |
| Validate workflow also published | Split `release-build.yml` vs `publish-apk.yml` |
| README-only triggered heavy Android CI | Path filters |
| Short SHA tag collisions | Full SHA tags historically; now SemVer + short SHA only in APK filename |
| `release: published` re-entry weirdness | Removed; publish only from push/dispatch |

## File map

| Neo | In your app |
| --- | --- |
| `.github/workflows/release-build.yml` | Validate |
| `.github/workflows/publish-apk.yml` | Publish |
| `scripts/ci/test_release_configuration.py` | Assert workflow invariants in CI |

## Validation

```bash
# Local regression of workflow text + scripts
python3 -m unittest discover -s scripts/ci -p 'test_*.py'

# On CI: actionlint + shellcheck (as in validate workflow)
```
