## 2024-07-12 - Avoid .lowercase() in Kotlin Collections
**Learning:** Calling `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations, causing O(N log N) overhead in sorts and unnecessary garbage collection.
**Action:** Optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching.
