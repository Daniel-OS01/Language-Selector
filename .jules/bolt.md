## 2026-04-22 - Optimize string allocation and sorting in MainScreenVm
**Learning:** Chained `.sortedBy` combined with `.lowercase()` creates multiple intermediate list allocations and unnecessary string allocations during list sorting. This is especially prevalent when sorting app lists in UI ViewModels.
**Action:** Use `.sortedWith(compareBy(...).thenBy(String.CASE_INSENSITIVE_ORDER) {...})` for sorting and `.contains(..., ignoreCase = true)` for filtering to eliminate string allocations.
