## 2024-05-24 - Avoid `.lowercase()` in Kotlin collection operations
**Learning:** Calling `.lowercase()` inside `.sortedBy` or `.filter` in Kotlin creates significant intermediate string allocations, introducing O(N log N) overhead for sorts and O(N) overhead for filters.
**Action:** Optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching. For chained sorts, reverse the order and use `.thenBy` to combine into a single pass.
