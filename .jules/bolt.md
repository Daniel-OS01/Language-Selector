## 2024-04-12 - Dependency Addition Restrictions and Test Environment
**Learning:** Adding new dependencies to version catalogs (`libs.versions.toml`) requires explicit permission. Pre-existing failing unit tests due to missing dependencies (`junit`) should not be resolved by adding those dependencies unless instructed, to prevent scope creep and invalid changes.
**Action:** Do not bypass the version catalog or attempt to fix broken tests by adding unauthorized dependencies. Let pre-existing tests fail or use selective exclusions if unrelated to the requested scope.
