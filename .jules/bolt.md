## 2024-06-04 - Unnecessary String allocations in filter/sort
**Learning:** In Kotlin, using `.lowercase()` during collection filtering or sorting creates unnecessary intermediate string allocations.
**Action:** Use `String.CASE_INSENSITIVE_ORDER` for sorting operations and `.contains(..., ignoreCase = true)` for string searches to prevent unnecessary memory overhead.
