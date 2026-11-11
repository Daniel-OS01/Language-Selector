## 2025-02-28 - Optimize Sorting and Filtering string operations

**Learning:** Avoid `String.lowercase()` string allocations when sorting strings by ignoring case using `String.CASE_INSENSITIVE_ORDER`. Also avoid creating intermediate lists when chaining `.sortedBy` calls by combining them with `.sortedWith` and `.thenBy`. Avoid allocations with filtering by using `String.contains` with `ignoreCase = true`.
**Action:** Use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` and `contains(..., ignoreCase = true)` to avoid string allocations. Use `.sortedWith(compareBy { ... }.thenBy { ... })` to avoid intermediate list allocations.
