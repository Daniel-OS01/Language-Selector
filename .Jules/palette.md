## 2024-05-17 - Prevent redundant TalkBack announcements in Jetpack Compose
**Learning:** In Jetpack Compose, state-indicating icons (like a checkmark in `FilterChip`) and decorative images (like an app icon) that are accompanied by descriptive text can result in redundant or repetitive announcements when navigating with TalkBack if they have a non-null `contentDescription`.
**Action:** Set `contentDescription = null` for these types of icons and images to ensure a cleaner and more efficient screen reader experience, as the adjacent text provides the necessary context.
