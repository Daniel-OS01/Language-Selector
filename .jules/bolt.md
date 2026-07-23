## 2024-07-23 - Avoid String allocations in Kotlin collections
**Learning:** Calling `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations (e.g., O(N log N) overhead in sorts). Chaining sorts (e.g., `.sortedWith(...).sortedBy(...)`) also allocates intermediate lists unnecessarily.
**Action:** Use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for case-insensitive sorting, `.contains(..., ignoreCase = true)` for searching, and combine multiple sorts into a single pass using `.thenBy(...)`.
