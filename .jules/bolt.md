## 2024-08-09 - Kotlin Sort Optimization
**Learning:** Avoid using `.lowercase()` inside collection operations like `.sortedBy` or `.filter` in Kotlin. It creates significant intermediate string allocations (e.g., O(N log N) overhead in sorts). Also avoid chaining multiple sorts which allocate intermediate lists.
**Action:** Use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching. Combine multiple sorts into a single pass using `.thenBy(...)`.
