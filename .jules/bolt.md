## 2024-07-27 - [Kotlin Sorting Overhead]
**Learning:** Chaining `.sortedBy()` calls allocates multiple intermediate lists, and calling `.lowercase()` inside the sorting lambda creates massive O(N log N) string allocations per sort pass.
**Action:** Use `.sortedWith()` combining conditions using `.thenBy()`, and utilize `String.CASE_INSENSITIVE_ORDER` to eliminate allocations. For chained stable sorts (`.sortedBy { X }.sortedBy { Y }`), the correct single pass reverses the logic: `compareBy { Y }.thenBy { X }`.
