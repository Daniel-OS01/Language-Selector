## 2024-10-24 - Prevent unnecessary string allocations
**Learning:** Chaining `.sortedBy { it.lowercase() }` and using `.lowercase().contains(...)` creates an intermediate `String` object for every element in the list, increasing memory pressure and causing GC churn, which is detrimental to smooth UI rendering in Android.
**Action:** Use `String.CASE_INSENSITIVE_ORDER` with `.sortedWith(compareBy(...).thenBy(...))` for sorting and `.contains(..., ignoreCase = true)` for searching to avoid intermediate allocations and reduce GC overhead.
