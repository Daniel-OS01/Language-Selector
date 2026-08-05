## 2025-02-18 - Optimize sorting string allocations
**Learning:** Using `.lowercase()` inside collection operations like `.sortedBy` creates massive intermediate string allocations in Kotlin. Chaining multiple sorts also allocates intermediate lists.
**Action:** Use `sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { ... })` for single-pass sort without string allocations.
