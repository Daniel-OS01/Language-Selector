## 2024-07-21 - Avoid intermediate string allocations in Kotlin collections
**Learning:** Using `.lowercase()` inside collection operations like `.sortedBy` or `.filter` in Kotlin creates significant intermediate string allocations (O(N log N) overhead in sorts). Chaining sorts (e.g., `.sortedBy(...).sortedBy(...)`) allocates intermediate lists.
**Action:** Use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching. Combine multiple sorts with `.thenBy(...)` instead of chaining.
