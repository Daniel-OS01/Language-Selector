## 2024-06-21 - Optimize string comparisons in Kotlin collections
**Learning:** Using `.lowercase()` during collection filtering or sorting in Kotlin creates unnecessary intermediate string allocations, impacting memory overhead and performance, especially on large lists.
**Action:** Use `.contains(..., ignoreCase = true)` for string searches and `String.CASE_INSENSITIVE_ORDER` for sorting operations to avoid intermediate object allocations.
