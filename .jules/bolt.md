## 2024-05-14 - Optimize Kotlin Collection Operations

**Learning:** Calling `.lowercase()` inside collection operations like `.sortedBy` or `.filter` creates significant intermediate string allocations (e.g., O(N log N) overhead in sorts). This can cause noticeable GC pauses, especially on Android devices.
**Action:** Always optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching.
