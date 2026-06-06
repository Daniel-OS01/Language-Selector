## 2024-06-06 - Stable Sorting Migration
**Learning:** When migrating from chained `sortedBy` calls to `sortedWith` to prevent intermediate list allocations, the order of sort keys must be reversed. `sortedBy` is a stable sort, meaning the last applied `sortedBy` determines the primary sort key.
**Action:** Always reverse the property order when converting `list.sortedBy { a }.sortedBy { b }` into `list.sortedWith(compareBy { b }.thenBy { a })` to perfectly preserve the original list ordering behavior.
