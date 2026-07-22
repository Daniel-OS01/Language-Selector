## 2024-05-24 - Avoid string allocations in collection operations
**Learning:** Using `.lowercase()` inside collection operations like `.sortedBy` or `.filter` in Kotlin creates O(N log N) or O(N) intermediate string allocations, causing unnecessary GC pressure and slowdowns. Also, chaining `.sortedBy().sortedBy()` creates unnecessary intermediate lists.
**Action:** Use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching. Combine multiple sorts with `.thenBy(...)` to avoid allocating intermediate lists.
