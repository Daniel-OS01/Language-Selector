## 2024-05-24 - Remove intermediate String allocations in Kotlin string searches and sorting
**Learning:** Calling `.lowercase()` on strings within `filter` or `sortedBy` creates unnecessary intermediate `String` allocations, heavily impacting memory overhead for larger datasets. Furthermore, using `.lowercase()` is not efficient.
**Action:** Use `.contains(searchString, ignoreCase = true)` for string searches to avoid extra allocations and `String.CASE_INSENSITIVE_ORDER` comparator for sorting.
