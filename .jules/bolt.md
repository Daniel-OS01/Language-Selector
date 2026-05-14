## 2024-05-14 - Optimize App List Sorting in MainScreenVm
**Learning:** Chaining `sortedBy` creates intermediate lists, and using `lowercase()` inside loops for filtering or sorting causes unnecessary String allocations.
**Action:** Use `.sortedWith(compareBy(...).thenBy(String.CASE_INSENSITIVE_ORDER) { ... })` instead of chained `sortedBy`, and use `contains(..., ignoreCase = true)` for searching.
