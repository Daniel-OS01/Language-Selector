## 2024-06-27 - Avoid intermediate String allocations during collection operations
**Learning:** Using `.lowercase()` on string properties during collection sorting (e.g., `sortedBy { it.name.lowercase() }`) or filtering inside a loop allocates unnecessary intermediate String objects. In an application dealing with large lists like installed packages, this causes measurable GC pressure and reduces performance.
**Action:** Use `String.CASE_INSENSITIVE_ORDER` for sorting operations and `.contains(..., ignoreCase = true)` for search filtering to avoid these allocations.
