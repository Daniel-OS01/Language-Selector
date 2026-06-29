## 2026-03-25 - Prevent redundant TalkBack announcements in Compose FilterChips
**Learning:** In Jetpack Compose, icons within components that inherently manage their own accessibility state (such as the 'Done' icon in a selected FilterChip) can cause redundant TalkBack announcements if they have a contentDescription.
**Action:** Set `contentDescription = null` for icons within self-managing accessibility components like FilterChip, and for decorative icons accompanied by adjacent descriptive text.

## 2026-03-25 - Semantic Roles for Clickable Rows
**Learning:** Using the `clickable` modifier on a Row (or any generic composable) without specifying a semantic role results in generic TalkBack feedback.
**Action:** When making a generic layout clickable, set `role = Role.Button` and provide an `onClickLabel` with a descriptive localized verb to ensure proper screen reader pronunciation and interaction hints.
