## 2024-04-24 - Intermediate Collections and String Allocations in View Models
**Learning:** Chaining sorting operations with `sortedBy` creates intermediate collections and can involve unnecessary string allocations (like `.lowercase()`), impacting performance in UI state updates.
**Action:** Use `sortedWith(compareBy(...).thenBy(...))` to combine sorting logic into a single operation, and utilize `String.CASE_INSENSITIVE_ORDER` and `contains(..., ignoreCase = true)` to perform case-insensitive comparisons without allocating new string instances.
