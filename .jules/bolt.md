## 2026-07-09 - Avoid string allocations in collection operations
**Learning:** Using `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations (e.g., O(N log N) overhead in sorts), hurting performance in large collections.
**Action:** Optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching instead of converting the string to lowercase first.
