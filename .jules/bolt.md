## 2024-07-16 - Prevent unnecessary intermediate allocations in Kotlin string sorting and searching
**Learning:** `lowercase()` inside collections operations like `.sortedBy` and `.filter` creates significant intermediate string allocations (e.g., O(N log N) overhead in sorts) in Kotlin.
**Action:** Use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for searching.
