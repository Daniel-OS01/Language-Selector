## 2024-06-28 - Avoid .lowercase() in loops and sorting
**Learning:** Using `.lowercase()` in Kotlin collection filtering (`filter`) or sorting (`sortedBy`) operations creates new string allocations for every element, which can cause significant memory overhead and GC pressure on large collections like installed apps lists.
**Action:** Use `.contains(..., ignoreCase = true)` for string searches and `.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting to perform case-insensitive operations without allocating new strings.
