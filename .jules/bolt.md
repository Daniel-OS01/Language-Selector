## 2024-07-17 - Avoid Intermediate Object Allocations During Collection Operations

**Learning:** When sorting collections in Kotlin based on case-insensitive strings (e.g., `sortedBy { it.name.lowercase() }`), calling `.lowercase()` creates an intermediate string allocation for every element being compared. In an O(N log N) sorting operation, this creates significant memory overhead and CPU churn. Furthermore, chaining multiple collection transformations (e.g., `sortedWith(...).sortedBy(...)`) allocates multiple intermediate lists.

**Action:**
- Use `String.CASE_INSENSITIVE_ORDER` inside `.sortedWith(compareBy(...))` to sort strings case-insensitively without creating new allocations.
- When searching (e.g., using `.filter`), use `.contains(query, ignoreCase = true)` rather than converting both the query and the target to `.lowercase()`.
- Instead of chaining multiple sorts (which creates intermediate lists), use a single pass sort with `.thenBy(...)` (e.g., `sortedWith(compareBy<Type> { ... }.thenBy { ... })`).
