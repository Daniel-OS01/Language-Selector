## 2024-08-08 - Optimize string allocations in sorting and filtering
**Learning:** In Kotlin, calling `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations (e.g., O(N log N) overhead in sorts). Also, chaining `.sortedBy` calls creates intermediate lists.
**Action:** Optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching. Combine multiple sorts into a single pass using `.thenBy(...)`.
