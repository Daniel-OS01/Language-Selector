## 2024-05-23 - Avoid String Allocation in Kotlin sort
**Learning:** `sortedBy { it.name.lowercase() }` and `.contains(normalizedQuery, ignoreCase = true)` rather than converting both to `.lowercase()` saves unnecessary string allocations and prevents intermediate objects, speeding up sorts/filters on large lists.
**Action:** Use `.contains(..., ignoreCase = true)` and `String.CASE_INSENSITIVE_ORDER` where applicable in Kotlin for collections and strings.
