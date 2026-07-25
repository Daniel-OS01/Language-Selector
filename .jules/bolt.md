## 2024-07-25 - Avoid Chained Sorts and `.lowercase()` in Collections
**Learning:** The codebase chained `.sortedBy()` calls and used `.lowercase()` inside collection operations (e.g., `apps.sortedBy { it.name.lowercase() }.sortedBy { !it.isModified() }`), which creates intermediate list allocations and string allocations during sorting.
**Action:** Consolidate chained sorts into a single pass using `.sortedWith(compareBy { ... }.thenBy { ... })` (reversing order to maintain stability) and avoid `.lowercase()` inside loops by utilizing `String.CASE_INSENSITIVE_ORDER`.
