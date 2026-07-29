## 2026-05-02 - Redundant TalkBack announcements in Jetpack Compose
**Learning:** In Jetpack Compose, state-indicating components like `FilterChip` already announce their state, and components accompanied by explicit text (like `AlertDialog` with titles/text, or `QuickTextButton`) shouldn't have redundant `contentDescription` on their icons.
**Action:** Set `contentDescription = null` on icons that are accompanied by descriptive text or state indications to prevent redundant TalkBack announcements.
