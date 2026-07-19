## 2024-07-19 - Optimize string allocations in collection operations
**Learning:** In Kotlin, using `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations, resulting in overhead. Chaining sorts (e.g., `.sortedBy(...).sortedBy(...)`) also allocates intermediate lists.
**Action:** Optimize performance by using `String.CASE_INSENSITIVE_ORDER` for sorting, `ignoreCase = true` for searching, and `.thenBy` to combine multiple sort passes into a single pass.
