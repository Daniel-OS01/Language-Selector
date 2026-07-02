## 2024-07-02 - Avoid lowercase in collection operations
**Learning:** Calling .lowercase() inside collection operations like .sortedBy or .filter creates significant intermediate string allocations (e.g. overhead in sorts).
**Action:** Optimize performance by using sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }) for sorting and .contains(normalizedQuery, ignoreCase = true) for searching.
