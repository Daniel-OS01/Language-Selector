# Playbook 08 — JVM unit tests on Android projects

## Problem / when you need this

`./gradlew testDebugUnitTest` fails with:

```text
Method put in org.json.JSONObject not mocked.
```

or similar stub exceptions for `android.*` / framework classes. Pure JVM unit tests use the **android.jar stubs**, not a real device runtime.

## Recommended architecture

| Kind of code | Test approach |
| --- | --- |
| Pure Kotlin (parsers, bump math, planning) | JVM unit tests |
| `org.json` / small Android-shaped APIs | Add **real** JVM dependency for tests **or** Robolectric |
| Compose UI / Activity | Instrumentation (`androidTest`) — heavier |
| Shell/CI scripts | Python `unittest` or bats + ShellCheck |

For JSON codecs shared with Android:

```kotlin
// app/build.gradle.kts
testImplementation("org.json:json:20250517")
```

This shadows the stub implementation on the unit-test classpath.

## Concrete checklist

- [ ] Keep business logic out of Android framework types when possible
- [ ] Add explicit test deps for anything that hits stubs
- [ ] Assert error mapping (`IllegalArgumentException`) not raw framework exceptions
- [ ] Prefer temp git repos / subprocess for shell scripts (Neo SemVer tests)
- [ ] If Ruff/Bandit flags `subprocess` in tests, file-level `# ruff: noqa: S603, S607` is OK for intentional CI harnesses

## Pitfalls we hit + fixes (Neo)

| Pitfall | Fix |
| --- | --- |
| `LocaleBackupCodecTest` failed on `JSONObject.put` | `testImplementation("org.json:json:…")` |
| Wanted Compose pinning UI test | Skipped — no Compose test helpers; covered `LocaleManager` in JVM instead |
| Ruff S603/S607 on git/bash subprocess in CI tests | `# ruff: noqa: S603, S607` at top of `test_release_configuration.py` |

## File map

| Neo | In your app |
| --- | --- |
| `app/build.gradle.kts` test deps | Unit-test classpath |
| `app/src/test/java/...` | JVM tests |
| `scripts/ci/test_release_configuration.py` | Workflow/script tests outside Gradle |

## Validation

```bash
./gradlew --no-daemon testDebugUnitTest
python3 -m unittest discover -s scripts/ci -p 'test_*.py'
```
