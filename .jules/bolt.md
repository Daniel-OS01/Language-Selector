## 2026-05-07 - Prevent String Allocations in Sort/Filter
**Learning:** In Kotlin UI ViewModels (like MainScreenVm), chaining `.sortedBy` or repeatedly calling `.lowercase()` inside filters creates thousands of temporary `String` objects and intermediate lists, causing unnecessary GC pressure during rapid UI state changes (like searching).
**Action:** Always combine chained `.sortedBy` into a single `.sortedWith(compareBy<T>{}.thenBy(String.CASE_INSENSITIVE_ORDER){})` pass. Never allocate inside a filter block; use `.contains(..., ignoreCase = true)` instead of `.lowercase().contains()`.
