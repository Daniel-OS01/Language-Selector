## 2024-07-01 - Avoid lowercase() inside Kotlin collections operations
**Learning:** Using `.lowercase()` inside collection functions like `sortedBy` or `filter` on Android causes severe memory bloat because it allocates a new String for every comparison, leading to heavy string creations during sorts and filtering.
**Action:** Always use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` and `.contains(..., ignoreCase = true)` to avoid unnecessary GC pressure and UI stutter on large lists.
