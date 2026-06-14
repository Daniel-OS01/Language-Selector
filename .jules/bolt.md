## 2024-05-24 - Avoid `.lowercase()` for sorting and filtering
**Learning:** Using `.lowercase()` in tight loops (like sorting and filtering lists of apps) creates unnecessary intermediate string objects in memory, which can lead to GC pauses and stuttering during search and list loading.
**Action:** Replace `.lowercase().contains(text.lowercase())` with `.contains(text, ignoreCase = true)` and `sortedBy { it.lowercase() }` with `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })`.
