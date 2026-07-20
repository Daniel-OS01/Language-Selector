## 2024-05-24 - Kotlin String Allocation Overhead in Collection Operations
**Learning:** Calling `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations (e.g., O(N log N) overhead in sorts). Chaining multiple sorts like `.sortedWith(...).sortedBy(...)` also allocates intermediate lists.
**Action:** Optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching. Combine chained sorts into a single pass using `.thenBy(...)`.
