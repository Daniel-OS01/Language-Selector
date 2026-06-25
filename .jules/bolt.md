## 2026-06-25 - Prevent memory overhead from lowercase() in Kotlin collections
**Learning:** Using .lowercase() inside collection sorting or filtering creates new String objects on every iteration, leading to unnecessary memory allocation and GC pressure.
**Action:** Use String.CASE_INSENSITIVE_ORDER for sorting and .contains(ignoreCase = true) for search operations to avoid intermediate string allocations.
