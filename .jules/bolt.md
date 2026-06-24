## 2024-06-24 - Avoid `.lowercase()` in Kotlin string operations
**Learning:** Using `.lowercase()` in Kotlin collections (like `filter` or `sortedBy`) causes unnecessary intermediate string allocations, creating memory pressure, especially inside loops or frequent operations like filtering search results.
**Action:** Use `.contains(..., ignoreCase = true)` for string searches and `String.CASE_INSENSITIVE_ORDER` for sorting operations to avoid intermediate string allocations.
