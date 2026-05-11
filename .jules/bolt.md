## 2024-05-20 - Prevent String Allocations in UI State Loops
**Learning:** Chained `.sortedBy { it.lowercase() }` and `.lowercase().contains()` calls in Jetpack Compose ViewModels trigger massive GC churn during rapid state updates (like typing in a search bar), causing UI stuttering due to continuous string allocation.
**Action:** Always combine `.sortedBy` chains into a single `.sortedWith` comparator, and leverage `String.CASE_INSENSITIVE_ORDER` and `.contains(..., ignoreCase = true)` to perform native character comparisons without allocating new string objects in memory.
