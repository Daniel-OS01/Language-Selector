## 2024-06-20 - Prevent memory overhead from `.lowercase()`
**Learning:** Using `.lowercase()` during collection filtering or sorting introduces unnecessary memory overhead from intermediate string allocations in Kotlin.
**Action:** Use `.contains(..., ignoreCase = true)` for string searches and `String.CASE_INSENSITIVE_ORDER` for sorting operations.
