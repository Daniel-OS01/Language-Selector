## 2024-07-31 - Optimize Kotlin Collection Operations
**Learning:** In Kotlin, using `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations. Chaining multiple sorts (e.g., `.sortedWith(...).sortedBy(...)`) allocates intermediate lists.
**Action:** Use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching. Combine multiple sorts into a single pass using `.thenBy(...)` in reverse order of the original chained `.sortedBy()` calls.
