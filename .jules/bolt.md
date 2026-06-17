## 2024-06-17 - Avoid intermediate string allocations in Kotlin
**Learning:** Using `.lowercase()` during collection filtering or sorting creates unnecessary intermediate string allocations, causing memory overhead.
**Action:** Use `.contains(..., ignoreCase = true)` for string searches and `String.CASE_INSENSITIVE_ORDER` for sorting operations.
