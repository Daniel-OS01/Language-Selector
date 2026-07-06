## 2024-05-14 - Eliminate intermediate string allocations in Kotlin
**Learning:** In Kotlin, calling `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations (e.g., O(N log N) overhead in sorts).
**Action:** Optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching.
