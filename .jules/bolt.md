## 2024-05-12 - Optimize Kotlin sorting and filtering
**Learning:** Chained `sortedBy` creates intermediate lists and `String.lowercase()` allocates strings unnecessarily during iterations.
**Action:** Use `sortedWith(compareBy(...).thenBy(...))` for complex sorting and `String.CASE_INSENSITIVE_ORDER` or `contains(..., ignoreCase = true)` to avoid string allocations during matching/sorting.
