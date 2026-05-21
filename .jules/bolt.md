## $(date +%Y-%m-%d) - Prevent allocations in Kotlin list processing
**Learning:** Chaining `.sortedBy` creates intermediate lists, and `.lowercase()` inside filter/sort closures creates many short-lived `String` objects, triggering unnecessary garbage collection pauses during frequent list operations (like searching).
**Action:** Use `.sortedWith(compareBy(...).thenBy(...))` to sort with a single pass, and substitute `.lowercase()` with `String.CASE_INSENSITIVE_ORDER` and `.contains(..., ignoreCase = true)` to avoid allocations.
