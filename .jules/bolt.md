## 2024-07-18 - Avoid intermediate allocations in collection operations
**Learning:** Using `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations (e.g., O(N log N) overhead in sorts). Also, chaining multiple sorts (e.g., `.sortedBy(...).sortedBy(...)`) allocates intermediate lists.
**Action:** Optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting, `.contains(..., ignoreCase = true)` for searching, and combining sorts with `.thenBy(...)`.
