## 2024-05-15 - Case-insensitive string handling optimization
**Learning:** In Jetpack Compose ViewModels or anywhere lists are heavily processed, using `.lowercase()` for case-insensitive filtering or sorting allocates a new `String` for every item, creating unnecessary garbage collection pressure and CPU overhead.
**Action:** Use `.contains(..., ignoreCase = true)` for searching and `String.CASE_INSENSITIVE_ORDER` inside `.sortedWith(compareBy(...).thenBy(...))` when sorting by string values.
