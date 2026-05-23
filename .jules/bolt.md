
## 2024-05-18 - String Sorting Optimization
**Learning:** In Kotlin UI ViewModels (especially Jetpack Compose), chained `.sortedBy { it.lowercase() }` calls create unnecessary intermediate string allocations and list collections, leading to higher memory pressure on every list update or reload.
**Action:** Replace `.sortedBy { it.name.lowercase() }.sortedBy { !it.isModified() }` with `.sortedWith(compareBy<AppInfo> { !it.isModified() }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })` to perform sorting in a single pass without extra string allocations. Use `.contains(..., ignoreCase = true)` for searching.
