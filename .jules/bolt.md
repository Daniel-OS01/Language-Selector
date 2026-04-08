## 2026-04-08 - [Combine sortedBy into sortedWith and avoid lowercase() allocations]
**Learning:** Chaining `.sortedBy` generates intermediate lists, and using `.lowercase()` for case-insensitive sort/search creates many string allocations.
**Action:** Use `.sortedWith(compareBy<Type> {...}.thenBy(String.CASE_INSENSITIVE_ORDER) {...})` and `.contains(..., ignoreCase = true)`.
