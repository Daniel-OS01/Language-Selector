## 2024-11-20 - String Allocation Overheads in ViewModels
**Learning:** Chained `.sortedBy` calls and widespread `.lowercase()` use for filtering/searching in ViewModels (like MainScreenVm) cause significant intermediate list generations and redundant string allocations.
**Action:** Always combine chained sorting into `.sortedWith(compareBy<T> { ... }.thenBy(String.CASE_INSENSITIVE_ORDER) { ... })` and use `.contains(..., ignoreCase = true)` to avoid unnecessary GC pressure.
