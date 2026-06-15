## 2024-06-15 - Prevent Unnecessary String Allocations in Loops
**Learning:** In Kotlin, using `.lowercase()` on strings during collection filtering or sorting creates intermediate String objects on every iteration, leading to significant unnecessary memory overhead and garbage collection pauses, especially with large lists.
**Action:** Avoid `.lowercase()` during collection operations. Instead, use `.contains(..., ignoreCase = true)` for string searches and `String.CASE_INSENSITIVE_ORDER` for case-insensitive sorting operations.
