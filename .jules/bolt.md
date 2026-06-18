## 2024-06-18 - Avoid unnecessary lowercase allocations in Kotlin
**Learning:** Calling `.lowercase()` on strings during large collection sorting and filtering creates unnecessary string allocations that can pressure memory and reduce performance on Android.
**Action:** Use `String.CASE_INSENSITIVE_ORDER` for sorting and `.contains(..., ignoreCase = true)` for searching instead of creating new lowercase string objects.
