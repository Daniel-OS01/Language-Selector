## 2024-06-07 - Avoid intermediate String allocations in collections
**Learning:** In Kotlin, using `.lowercase()` during collection filtering or sorting creates intermediate string allocations. This causes unnecessary memory overhead.
**Action:** Use `.contains(..., ignoreCase = true)` for string searches and `String.CASE_INSENSITIVE_ORDER` with `compareBy` for sorting operations to avoid intermediate string allocations.
