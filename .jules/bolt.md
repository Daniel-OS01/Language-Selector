## 2024-07-07 - Optimize string operations in collection processing
**Learning:** Using `.lowercase()` inside collection operations like `.sortedBy` and `.filter` creates significant intermediate string allocations and overhead (e.g., O(N log N) overhead in sorts).
**Action:** Optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching.
