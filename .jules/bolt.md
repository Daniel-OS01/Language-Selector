## 2024-06-26 - Avoid intermediate allocations with String operations in loops
**Learning:** Calling `.lowercase()` on strings during collection sorting and filtering generates unnecessary intermediate string allocations, creating GC pressure and impacting performance, particularly in list operations or loops.
**Action:** Use `.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting and `.contains(..., ignoreCase = true)` for string filtering to avoid these allocations.
