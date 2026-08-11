## 2025-02-20 - Kotlin sorting string allocation overhead
**Learning:** In Kotlin, using `.lowercase()` inside collection operations like `.sortedBy` creates significant intermediate string allocations (O(N log N) overhead). Additionally, chaining multiple `.sortedBy` calls allocates intermediate lists.
**Action:** Optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` and combine chained sorts into a single pass using `.thenBy(...)`.
