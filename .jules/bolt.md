## 2024-06-19 - Avoid .lowercase() in Collection Operations
**Learning:** Calling `.lowercase()` on strings during large collection operations like sorting or filtering (e.g. `list.sortedBy { it.name.lowercase() }` or `filter { it.name.lowercase().contains(...) }`) causes unnecessary memory allocations and is inefficient.
**Action:** Use string matching methods that ignore case (e.g., `contains(..., ignoreCase = true)`) for filtering, and case-insensitive string comparators (e.g., `String.CASE_INSENSITIVE_ORDER`) for sorting to eliminate unnecessary string creation.
