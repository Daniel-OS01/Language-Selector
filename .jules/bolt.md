## 2024-05-18 - String Allocation Optimization in Kotlin

**Learning:** When sorting or filtering strings in Kotlin, chaining operations like `.sortedBy { it.lowercase() }` or calling `.lowercase().contains(...)` creates unnecessary intermediate string allocations. These allocations can negatively impact performance, especially when dealing with large lists or frequent search queries.

**Action:** To optimize string sorting without allocations, use `sortedWith` with `compareBy` and `String.CASE_INSENSITIVE_ORDER`: `sortedWith(compareBy<Type> { ... }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.stringProperty })`. For filtering and searching, use the `ignoreCase` parameter in functions like `contains` instead of allocating new lowercase strings: `it.stringProperty.contains(searchQuery, ignoreCase = true)`.
