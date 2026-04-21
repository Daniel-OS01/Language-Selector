## 2024-05-18 - Avoid string allocations in view models
**Learning:** Chained `.sortedBy { it.lowercase() }` calls create intermediate lists and per-item string allocations which hit performance frequently in view models, especially during list filtering.
**Action:** Use `.sortedWith(compareBy(...).thenBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting strings case-insensitively without creating new allocations, and use `.contains(..., ignoreCase = true)` for string filtering. Explicitly define the generic type for `compareBy<T>` if type inference fails when chained.
