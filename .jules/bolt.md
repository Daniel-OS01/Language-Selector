
## 2024-05-23 - Avoid String Allocations in Sorting and Filtering
**Learning:** In Kotlin, mapping strings with `.lowercase()` during sorting and filtering (e.g., `it.name.lowercase()`) causes significant, unnecessary string allocations inside loops, which impacts garbage collection and performance—especially on Android. Also, chaining `.sortedBy {}` calls creates intermediate lists.
**Action:** Instead of `sortedBy { ... }.sortedBy { ... }` with `.lowercase()`, use `sortedWith(compareBy<Type> { ... }.thenBy(String.CASE_INSENSITIVE_ORDER) { ... })`. For searching strings, use `.contains(query, ignoreCase = true)` rather than converting strings to lowercase.
