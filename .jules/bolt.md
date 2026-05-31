## 2026-05-31 - String Allocation Optimizations
**Learning:** In Kotlin, using `.lowercase()` during collection filtering or sorting creates unnecessary intermediate string allocations, impacting memory and performance, especially on large collections like installed app lists.
**Action:** Replaced chained `.sortedBy` and `.lowercase()` with `.sortedWith(compareBy<T> { ... }.thenBy(String.CASE_INSENSITIVE_ORDER) { ... })` and used `.contains(..., ignoreCase = true)` for searches to prevent string duplication.
