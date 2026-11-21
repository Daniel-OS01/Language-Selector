# Playbook 04 — Lint, i18n, and string formats

## Problem / when you need this

`lintRelease` fails the build with:

- `StringFormatMatches` — locale A uses `%2$d`, locale B uses `%2$s`
- `MissingTranslation` — new English keys absent from other `values-*` (severity can be **error**)

## Recommended architecture

1. Treat **default** `values/strings.xml` as the source of truth for format args and key set.
2. When changing a format string, update **all** translations of that key in the same PR.
3. When adding keys used in UI, either:
   - add translations for every shipped locale, or
   - mark `translatable="false"` if intentionally English-only, or
   - lower lint severity (team policy — Neo chose to translate).

About / version display pattern:

```kotlin
stringResource(R.string.version).format(
    BuildConfig.VERSION_NAME,  // %1$s
    BuildConfig.VERSION_CODE   // %2$d  ← must be %d everywhere
)
```

## Concrete checklist

- [ ] After editing a format string, `rg 'name="that_key"' app/src/main/res`
- [ ] Run `./gradlew lintRelease` before merge
- [ ] Know your lint severity for `MissingTranslation` (warning vs error)
- [ ] Prefer positional args (`%1$s`) when order differs by language

## Pitfalls we hit + fixes (Neo)

| Pitfall | Fix |
| --- | --- |
| Bumped English `version` to `%2$d`; locales still `%2$s` | Align ja / pt-BR / zh-CN / zh-TW |
| Added ~15 Settings keys; lintRelease failed MissingTranslation | Add the same keys to all locale folders |
| Assumed MissingTranslation was “just a warning” | In this AGP setup it aborted the build |

## File map

| Neo | In your app |
| --- | --- |
| `app/src/main/res/values/strings.xml` | Default strings |
| `app/src/main/res/values-*/strings.xml` | Translations |
| `AboutScreen.kt` `.format(...)` | Call sites must match format types |

## Validation

```bash
./gradlew --no-daemon lintRelease
```

Inspect `app/build/reports/lint-results-release.html` / intermediate text report on failure.
