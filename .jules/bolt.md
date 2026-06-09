## 2024-05-14 - Optimize sorting operations in MainScreenVm
**Learning:** In Kotlin, chaining `sortedBy` creates intermediate collections and executing `.lowercase()` on strings during sort allocates many temporary strings leading to memory pressure.
**Action:** Use `sortedWith(compareBy<...>().thenBy(...))` avoiding string allocation by using `String.CASE_INSENSITIVE_ORDER`.
