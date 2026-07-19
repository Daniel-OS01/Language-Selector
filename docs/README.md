# Documentation

Guides for **Language Selector Neo** and reusable Android engineering playbooks distilled from this fork.

## Reading order

| Audience | Start here |
| --- | --- |
| Ship a signed APK | [runbooks/publish-signed-apk.md](runbooks/publish-signed-apk.md) |
| Understand SemVer tags after `sha-*` releases | [runbooks/migrate-from-sha-tags.md](runbooks/migrate-from-sha-tags.md) |
| Debug a Neo-specific failure | [neo/TROUBLESHOOTING.md](neo/TROUBLESHOOTING.md) |
| See what changed in this fork | [neo/CHANGELOG-FORK.md](neo/CHANGELOG-FORK.md) |
| Reuse patterns in another Android app | [playbooks/](playbooks/) (01 → 08) |

## Layout

```text
docs/
  README.md                 ← you are here
  neo/                      ← this product’s history and symptoms
  playbooks/                ← portable Android patterns
  runbooks/                 ← operator checklists
```

## Playbooks (portable)

| # | Topic |
| --- | --- |
| [01](playbooks/01-release-signing-ci.md) | Release signing CI, keystore secrets, apksigner |
| [02](playbooks/02-semver-android-versioning.md) | SemVer `versionName` / encoded `versionCode` |
| [03](playbooks/03-github-actions-android.md) | Validate vs publish, path filters, draft releases |
| [04](playbooks/04-lint-i18n-string-formats.md) | `StringFormatMatches`, MissingTranslation |
| [05](playbooks/05-coroutines-viewmodel-races.md) | Cancel-and-replace jobs, `CancellationException` |
| [06](playbooks/06-json-backup-prefs-patterns.md) | org.json backups, SharedPreferences, defensive decode |
| [07](playbooks/07-shizuku-privileged-ipc.md) | Shizuku API vs manager version, per-call isolation |
| [08](playbooks/08-unit-testing-android-jvm.md) | JVM unit tests vs Android stubs |

Each playbook follows the same template: problem → architecture → checklist → pitfalls we hit → file map → validation.

## Related in-repo material

- User-facing overview: [../README.md](../README.md)
- CI scripts: [`scripts/ci/`](../scripts/ci/)
- Historical plan notes: [`.omx/plans/`](../.omx/plans/) (not canonical docs)
