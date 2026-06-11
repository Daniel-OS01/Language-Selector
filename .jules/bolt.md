## 2024-06-11 - Avoid `.lowercase()` allocations in Kotlin string searches and sorting
**Learning:** Using `.lowercase()` on string properties within `.filter {}` or `.sortedBy {}` blocks creates a new string instance for every item, causing unnecessary memory allocations and GC overhead on large collections.
**Action:** Instead, use `String.contains(..., ignoreCase = true)` for searching, and `.sortedWith(compareBy<Type> { ... }.thenBy(String.CASE_INSENSITIVE_ORDER) { ... })` for sorting to maintain case-insensitive behavior without extra allocations.
