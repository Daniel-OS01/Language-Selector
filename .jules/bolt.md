## 2026-07-24 - Avoid .lowercase() in collection operations
**Learning:** Using `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations (O(N log N) overhead in sorts). Chaining multiple `.sortedBy` calls also allocates intermediate lists unnecessarily.
**Action:** Optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting, combining multiple sorts using `.thenBy(...)`, and using `.contains(..., ignoreCase = true)` for searching to avoid intermediate allocations.
