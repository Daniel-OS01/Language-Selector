## 2024-08-06 - Kotlin Collection Allocations
**Learning:** Using `.lowercase()` inside `.sortedBy` or `.filter` in Kotlin collections creates significant intermediate string allocations (e.g., O(N log N) overhead in sorts). Chaining `.sortedBy` also creates unnecessary intermediate list allocations.
**Action:** Use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... }.thenBy { ... })` and `.contains(..., ignoreCase = true)` to avoid string allocations and intermediate lists.
