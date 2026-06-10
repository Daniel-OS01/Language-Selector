## 2024-05-24 - Avoid Memory Overhead in Sorts/Searches
**Learning:** In Kotlin, using `.lowercase()` during sorting (`sortedBy { it.name.lowercase() }`) or filtering (`filter { it.name.lowercase().contains(...) }`) inside loops creates large numbers of unnecessary short-lived String objects, causing high memory churn and garbage collection pauses.
**Action:** Use `String.CASE_INSENSITIVE_ORDER` for case-insensitive sorting, and `.contains(..., ignoreCase = true)` for case-insensitive searching. This avoids intermediate string allocations completely.
