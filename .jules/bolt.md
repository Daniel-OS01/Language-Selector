## 2024-05-24 - Kotlin lowercase() performance
**Learning:** Using `.lowercase()` inside a `.sortedBy` block in Kotlin (e.g., `sortedBy { it.name.lowercase() }`) or `.filter` block creates significant unnecessary intermediate string allocations, slowing down processing of collections.
**Action:** Instead, optimize performance by using `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })` for sorting, and `.contains(..., ignoreCase = true)` for searching.
