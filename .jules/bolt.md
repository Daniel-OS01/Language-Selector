## 2024-05-24 - Kotlin String Allocations in Sorting and Filtering
**Learning:** Chaining `.sortedBy` with `.lowercase()` creates an O(N log N) overhead from intermediate string allocations during sorting. Additionally, `.sortedBy` chaining creates intermediate lists.
**Action:** Always combine sorts using `sortedWith(compareBy(...).thenBy(...))` in reverse order for stability, and use `String.CASE_INSENSITIVE_ORDER` or `.contains(..., ignoreCase = true)` to avoid `.lowercase()` string allocations.
