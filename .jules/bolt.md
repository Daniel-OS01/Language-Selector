## 2026-06-05 - String Allocation Optimization
**Learning:** Using `.lowercase()` in filtering or sorting causes unnecessary intermediate string allocations. Kotlin's `.contains(..., ignoreCase = true)` and `String.CASE_INSENSITIVE_ORDER` are more efficient alternatives.
**Action:** Always prefer `ignoreCase = true` or `String.CASE_INSENSITIVE_ORDER` when doing case-insensitive comparisons to save memory overhead.
