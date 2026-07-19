# Plan: Fix and harden `release-build.yml`

Status: implemented and locally verified; GitHub publish and on-device smoke tests remain external.

## Implementation result

- Shizuku API/provider now resolve as `13.1.5` from Maven Central, matching the API embedded by `thedjchi/Shizuku` v13.7.0.
- Both Android modules and CI consume the single `android.compileSdk=37.0` property.
- The workflow now separates read-only validation from signed publication, pins third-party actions, validates signing inputs, verifies APK signatures, and publishes draft-first with rerun-safe behavior.
- Repository-free unit tests cover the artifact verifier and compile-SDK configuration contract.
- A clean Android container build with a new `GRADLE_USER_HOME` passed `testDebugUnitTest lintRelease assembleRelease`; this also exposed and fixed one invalid R8 rule and missing Japanese/Brazilian Portuguese strings.

## Outcome

Restore reliable APK builds for users running `thedjchi/Shizuku` v13.7.0, add regression coverage for dependency publication and the Android release build, and make publishing conditional, signed, test-gated, observable, and idempotent.

## Evidence and root cause

- The failing dependency is declared as `dev.rikka.shizuku:api` and `provider` in `gradle/libs.versions.toml:10,34-35` and consumed by `app/build.gradle.kts:97-98`.
- `thedjchi/Shizuku` v13.7.0 is the installed manager APK version, not the Maven client-library version. Its release tag pins the `Shizuku-API` submodule at commit `37ebcd3`, whose `manifest.gradle` declares API version `13.1.5`.
- Maven Central publishes both `dev.rikka.shizuku:api:13.1.5` and `dev.rikka.shizuku:provider:13.1.5`; it does not publish either artifact at `13.7.0`.
- The failed run `29689196607` confirms Gradle tried the nonexistent `13.7.0` coordinate and then failed DNS lookup for `maven.rikka.app`. The earlier run `29689068280` confirms `13.1.5` resolved and advanced to Android AAR validation.
- That next failure was corrected in source by moving both modules to compile SDK 37 (`app/build.gradle.kts:11`, `hidden_api/build.gradle.kts:7`), but the workflow still explicitly installs platform 36 (`.github/workflows/release-build.yml:43-46`) and `gradle.properties:84-85` still suppresses warnings for SDK 36.
- The workflow currently assembles without running the existing unit tests, lint, workflow validation, or APK verification (`.github/workflows/release-build.yml:60-76`). It also grants write permission to the build job, ignores its own workflow changes, publishes from `work`, handles `release.published` by creating a separate SHA release, and silently falls back to debug signing when release secrets are absent (`app/build.gradle.kts:26-42`). The failing run showed all four signing inputs empty.
- `.github/workflows/dependabot-auto-merge.yml:32-38` dispatches the release workflow before an auto-merge necessarily occurs even though the eventual `main` push already triggers it, creating redundant or stale builds.

## Requirements

1. Keep the user's installed `thedjchi/Shizuku` app at v13.7.0; use the client API/provider version actually published and embedded by that fork.
2. Resolve Shizuku through Maven Central without relying on `maven.rikka.app`.
3. Run unit tests, lint, dependency availability checks, and the release assembly before any publish operation.
4. Validate changes to the release workflow itself and run the build on relevant Android/Gradle/CI paths while skipping documentation-only changes.
5. Never publish a debug-signed or unsigned APK as a release artifact.
6. Give build/test jobs read-only repository access; grant `contents: write` only to the publish job.
7. Preserve SHA-tagged snapshot releases on `main`, but do not publish from pull requests, `work` pushes, or manual dispatches targeting a non-`main` ref. Manual dispatch on `main` must require an explicit publish input.
8. A rerun for the same commit must not create duplicate releases or ambiguous assets; release tags use the full commit SHA to avoid short-SHA collisions.
9. Preserve the user's current worktree and avoid the existing WSL CRLF-only churn; semantic diffs must remain narrowly scoped.

## Acceptance criteria

- `gradle/libs.versions.toml` uses Shizuku client version `13.1.5`, while a nearby comment distinguishes it from `thedjchi/Shizuku` app v13.7.0.
- `settings.gradle.kts` contains no `maven.rikka.app` repository, no snapshots repository, and no duplicate raw Maven Central URL. JitPack is retained only for the group(s) that demonstrably require it, using a Gradle content filter.
- A cold dependency check verifies both configured Shizuku POMs exist on Maven Central and fails with the exact missing coordinate and URL when a nonexistent version such as `13.7.0` is substituted.
- Both Android modules obtain compile SDK 37 from one repository-owned value, and the workflow installs that same platform rather than hard-coding 36 separately. The stale unsupported-SDK suppression is removed.
- `python3 -m unittest` for the new dependency-verifier tests passes without third-party Python packages.
- `./gradlew --no-daemon --stacktrace testDebugUnitTest lintRelease assembleRelease` succeeds from a clean Gradle dependency cache.
- The workflow is valid under `actionlint`; all referenced actions are pinned to immutable commits (with version comments for Dependabot) or replaced by the preinstalled GitHub CLI.
- A pull request changing `gradle/libs.versions.toml`, an Android module, CI scripts, or `release-build.yml` runs the verification/build job but cannot publish. A README-only change does not run it.
- A `work` push builds/tests and uploads an Actions artifact but does not create a GitHub Release.
- A `main` push fails early with a clear signing-configuration error if any of the four release secrets is absent; with all secrets present, `apksigner verify --print-certs` succeeds before publishing exactly one APK.
- Publishing uses a draft-first flow; rerunning the same SHA either completes the existing draft or reports success when the published release already contains the expected asset. It never creates a second tag/release for that SHA.
- A final manual device smoke test installs the produced APK alongside `thedjchi/Shizuku` v13.7.0, grants Shizuku access, loads the app list, and changes one per-app language successfully.

## Implementation steps

### 1. Correct the Shizuku dependency/repository contract

Files:

- `gradle/libs.versions.toml:10,34-35`
- `settings.gradle.kts:1-19`

Changes:

- Restore the library version to `13.1.5`; keep the existing `dev.rikka.shizuku:api` and `provider` coordinates.
- Add a short comment explaining that the Maven client-library version is independent from the installed Shizuku manager app version, and that the fork's v13.7.0 release currently embeds API 13.1.5.
- Keep `google()` and `mavenCentral()` as the canonical repositories.
- Remove `maven.rikka.app/releases`, `maven.rikka.app/snapshots`, and the duplicate `https://repo1.maven.org/maven2` entry.
- Remove JitPack from `pluginManagement` unless a plugin-resolution check proves it is required. In dependency resolution, retain it only for `com.github.topjohnwu.libsu` via `content { includeGroup(...) }`, because the current libsu coordinates are not on Maven Central.

Proof:

- Run the targeted verifier from step 3 and a clean-cache Gradle release build.
- Inspect dependency output to confirm Shizuku resolves from Maven Central and no request is made to `maven.rikka.app`.

### 2. Eliminate Android SDK drift

Files:

- `gradle.properties:72-88`
- `app/build.gradle.kts:9-18`
- `hidden_api/build.gradle.kts:5-12`
- `.github/workflows/release-build.yml:40-46`

Changes:

- Add one explicit project property, `android.compileSdk=37.0`, and read it through Gradle providers in both modules using AGP's minor-API DSL.
- Keep target/min SDK semantics unchanged unless their existing values need the same deduplication; this task must not silently change supported devices or runtime behavior.
- Remove `android.suppressUnsupportedCompileSdk=36` rather than updating the suppression, so an unsupported AGP/SDK combination fails visibly.
- Read the compile-SDK property in the workflow, validate it is numeric, and install `platforms;android-$compileSdk`; keep an explicit compatible build-tools package only if the release build proves the runner does not provision one.

Proof:

- A test guard compares the platform requested by the workflow with the single Gradle property.
- Both `:app` and `:hidden_api` compile against 37 on a clean runner.

### 3. Add a fast dependency-publication regression test

New files:

- `scripts/ci/verify_shizuku_artifacts.py`
- `scripts/ci/test_verify_shizuku_artifacts.py`

Behavior:

- Use Python 3 standard-library `tomllib` to read the version and both module aliases from `gradle/libs.versions.toml`.
- Construct the Maven Central POM URLs from the coordinates and perform bounded GET requests with a descriptive user agent, timeout, and a small retry budget for transient network failures.
- Fail fast on 404 or mismatched aliases with a message that explicitly distinguishes manager-app and API-library versions.
- Unit-test successful resolution, a missing API or provider, malformed catalog data, and transient failure followed by success using mocks or a local HTTP server. Tests must never call the public network.
- Run the unit tests first and the live publication check second in CI. The Gradle build remains the authoritative integration test; this script exists to make failures immediate and diagnostic.

### 4. Refactor `release-build.yml` into test/build and publish gates

File:

- `.github/workflows/release-build.yml:1-76`

Trigger/filter changes:

- Add `pull_request` validation for `main` (and `work` if it accepts PRs).
- Replace the broad `paths-ignore` rule with an allowlist covering `app/**`, `hidden_api/**`, `gradle/**`, root Gradle files/properties/wrappers, `scripts/ci/**`, and `.github/workflows/release-build.yml`.
- Keep `push` builds for `main` and `work`; publish only for `refs/heads/main`.
- Keep `workflow_dispatch`, adding a boolean `publish` input that defaults to false.
- Remove `release: published`; it currently converts an existing release event into a different SHA release and can mark that snapshot latest.
- Retain per-ref concurrency, but ensure publish-capable `main` runs are not canceled after publishing begins. Set explicit job timeouts.

Test/build job:

- Default the workflow/token to `contents: read`.
- Check out, install Java 21, use the maintained Gradle setup action for wrapper validation/caching, install the dynamically selected Android platform, and lint workflows with a checksum/digest-pinned `actionlint` release.
- Run the new Python unit/live checks, then `./gradlew --no-daemon --stacktrace testDebugUnitTest lintRelease assembleRelease` (the project has only a debug unit-test variant).
- On publish-capable events, validate that all four signing secrets are nonempty before the Gradle build and decode the keystore into `RUNNER_TEMP`, not `$HOME`.
- Update `app/build.gradle.kts:26-42` so an absent CI signing config produces an unsigned validation artifact rather than silently selecting the debug key.
- Assert exactly one APK was produced. Upload it as an Actions artifact with `if-no-files-found: error`; upload test/lint/Gradle problem reports on failure for diagnosis.

Publish job:

- Depend on a successful test/build job and run only for `main` pushes or manual dispatch with `publish=true`.
- Grant only this job `contents: write`, download the exact named artifact, and run `apksigner verify --verbose --print-certs` before any GitHub mutation.
- Replace the mutable third-party release action with `gh release` using `GH_TOKEN: ${{ github.token }}`.
- Create the unique `sha-<full-sha>` release as a draft, attach the APK, then publish/mark latest only after upload succeeds. On rerun, complete an existing draft; if an immutable published release already has the expected asset, exit successfully; otherwise fail with a precise inconsistency rather than overwriting blindly.

### 5. Remove the redundant Dependabot dispatch

File:

- `.github/workflows/dependabot-auto-merge.yml:8-38`

Changes:

- Delete the `Trigger APK build` step. The Dependabot PR will be tested by the new `pull_request` trigger and the merged commit will be built/published by the `main` push trigger.
- Remove `actions: write`; retain only the permissions required to enable auto-merge.
- Confirm the repository's required-check rules cause auto-merge to wait for the release verification job. If branch protection is not configured, document that external setting as a remaining operational requirement rather than adding unsafe workflow logic.

### 6. Verify locally and through GitHub event paths

Local/static verification:

1. `git diff --ignore-space-at-eol --check` and a semantic diff review to prevent mass CRLF conversion.
2. `python3 -m unittest discover -s scripts/ci -p 'test_*.py'`.
3. `python3 scripts/ci/verify_shizuku_artifacts.py`.
4. `actionlint .github/workflows/*.yml`.
5. With a temporary clean `GRADLE_USER_HOME`, run `./gradlew --no-daemon --stacktrace testDebugUnitTest lintRelease assembleRelease`.
6. Inspect `releaseRuntimeClasspath` and the produced APK; confirm no unresolved dependencies, no Rikka-repository access, and no debug certificate on a publishable artifact.

GitHub verification:

1. Open a PR containing the implementation and confirm the workflow runs with read-only permissions, passes tests/lint/assembly, uploads a non-published Actions artifact, and creates no release.
2. Verify a docs-only PR is skipped and a workflow-only PR is validated.
3. Merge only after checks pass; confirm one `main` run, not a second Dependabot-dispatched run.
4. Confirm the signed APK is published under the expected SHA tag and `apksigner` output is recorded before publication.
5. Rerun that workflow and confirm idempotent behavior.
6. Install the APK on the user's phone and run the Shizuku permission/language-change smoke test.

## Risks and mitigations

- **App/API version confusion recurs:** keep the explanatory version-catalog comment and the Maven publication check; never derive the library version from the manager APK tag.
- **Maven Central is transiently unavailable:** use a bounded retry in the fast verifier and Gradle caching, but do not retry deterministic 404s or hide resolution failures.
- **JitPack remains an external point of failure:** restrict it to the libsu group and consider a separate dependency-migration task only if libsu becomes available from a more reliable repository.
- **Compile SDK changes drift from CI:** use one Gradle property consumed by both modules and the workflow, with no unsupported-SDK suppression.
- **Signing secrets are currently absent:** fail publish-capable events early and clearly; validation PR/work builds remain possible without secrets, but release publication cannot proceed until all four repository secrets are configured.
- **Published GitHub Releases may be immutable:** use draft-first publication and make reruns verify existing assets instead of assuming replacement is allowed.
- **The Gradle 10 deprecation/configuration-time warnings remain:** capture their reports and create a separate cleanup task unless they block this release; do not mix a broad Gradle modernization into this incident fix.
- **Runtime compatibility is inferred, not fully proven by Maven metadata:** complete the on-device smoke test against `thedjchi/Shizuku` v13.7.0 before declaring end-to-end success.
- **Worktree shows broad CRLF-only modifications:** re-read live files, edit only the listed files, preserve line endings, and reject a diff that includes unrelated content.

## Alternatives considered

- **Use `dev.rikka.shizuku:*:13.7.0` from another repository:** rejected because the fork's API module is still 13.1.5 and no 13.7.0 Maven artifacts are published.
- **Build the fork through JitPack or vendor its AARs:** rejected for this fix because it changes coordinates/supply-chain ownership and adds maintenance without evidence that the fork exposes a distinct 13.7.0 client API.
- **Keep all Rikka and duplicate Maven repositories as fallbacks:** rejected because a nonexistent coordinate still falls through to dead hosts, increasing latency and failure surface without providing the required artifact.
- **Only add a retry around `assembleRelease`:** rejected because the observed 404/DNS path is deterministic configuration error, and retries would delay the same failure while leaving signing/filter/publish risks intact.

## Primary references

- `thedjchi/Shizuku` v13.7.0 release: https://github.com/thedjchi/Shizuku/releases/tag/v13.7.0-thedjchi
- Fork API version at the release's pinned submodule commit: https://raw.githubusercontent.com/thedjchi/Shizuku-API/37ebcd3e45edf1d68975c70dd199350b434161f7/manifest.gradle
- Maven Central API/provider: https://central.sonatype.com/artifact/dev.rikka.shizuku/api and https://central.sonatype.com/artifact/dev.rikka.shizuku/provider
- GitHub workflow filters/syntax: https://docs.github.com/actions/using-workflows/workflow-syntax-for-github-actions
- GitHub token least privilege: https://docs.github.com/actions/reference/authentication-in-a-workflow
- GitHub artifacts: https://docs.github.com/actions/using-workflows/storing-workflow-data-as-artifacts
- GitHub Gradle workflow guidance: https://docs.github.com/actions/guides/building-and-testing-java-with-gradle

## Expected implementation diff

Modified:

- `gradle/libs.versions.toml`
- `settings.gradle.kts`
- `gradle.properties`
- `app/build.gradle.kts`
- `hidden_api/build.gradle.kts`
- `app/proguard-rules.pro`
- `app/src/main/res/values-ja/strings.xml`
- `app/src/main/res/values-pt-rBR/strings.xml`
- `.github/workflows/release-build.yml`
- `.github/workflows/dependabot-auto-merge.yml`

Added:

- `scripts/ci/verify_shizuku_artifacts.py`
- `scripts/ci/test_verify_shizuku_artifacts.py`
- `scripts/ci/test_release_configuration.py`

No application feature code, SDK target/minimum behavior, or existing unit-test semantics changed. Resource changes are limited to translations required by the new release lint gate.
