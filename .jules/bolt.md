## 2024-05-18 - Optimize chained string filtering and sorting

**Learning:** When sorting and filtering collections using strings, chained calls like `sortedBy { ... }.sortedBy { ... }` or filtering with intermediate `.lowercase()` allocations degrade memory performance on large data structures. The codebase uses this on a list of all installed packages, creating temporary lowercased strings per application and intermediate generic lists for each sorting step.
**Action:** Chain comparisons directly with `sortedWith(compareBy<Type> { ... }.thenBy(String.CASE_INSENSITIVE_ORDER) { ... })` and combine filtering strings with `.contains(..., ignoreCase = true)` to skip the extra string allocations and multi-pass allocations for `List`s.
