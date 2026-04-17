## 2024-05-18 - Optimize AppInfo sorting and filtering allocations
**Learning:** In Kotlin, chaining `sortedBy { it.name.lowercase() }.sortedBy { !it.isModified() }` is extremely inefficient for list refreshes and view models because it allocates intermediate lists and thousands of temporary lowercased strings.
**Action:** Always combine multi-criteria sorting into a single pass using `sortedWith(compareBy<Type> { ... }.thenBy(String.CASE_INSENSITIVE_ORDER) { ... })`. For filtering and searching, always use `.contains(query, ignoreCase = true)` rather than converting items to `.lowercase()`.
