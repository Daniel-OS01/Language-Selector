## 2024-07-10 - Avoid intermediate string allocations in Kotlin collection operations
**Learning:** Calling `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations, adding O(N log N) or O(N) overhead.
**Action:** Use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching to optimize performance and reduce memory pressure.
