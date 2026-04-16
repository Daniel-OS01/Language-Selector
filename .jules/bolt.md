## 2024-04-16 - Prevent unnecessary string allocations and intermediate lists in Kotlin

**Learning:** In Kotlin (especially Jetpack Compose view models), optimize list filtering and sorting by avoiding `.lowercase()` string allocations.
**Action:** Use `.contains(..., ignoreCase = true)` for searching and `String.CASE_INSENSITIVE_ORDER` when sorting strings. Combine chained `.sortedBy{}` calls into a single `.sortedWith(compareBy(...).thenBy(...))` to eliminate intermediate list generation.
