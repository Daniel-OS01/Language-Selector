## 2024-06-16 - Prevent Unnecessary String Allocations in Kotlin Collections
**Learning:** In Kotlin, using `.lowercase()` during collection operations like `sortedBy` or `filter` creates unnecessary intermediate `String` objects, which can cause memory overhead and garbage collection pressure, especially on large collections.
**Action:** Always prefer `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.propertyName })` and `contains(..., ignoreCase = true)` for case-insensitive operations instead of allocating new lowercase strings.
