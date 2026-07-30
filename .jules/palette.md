## 2024-05-24 - Redundant TalkBack announcements
**Learning:** In Jetpack Compose, icons within components that inherently manage their own accessibility state (such as the 'Done' icon in a selected FilterChip), or decorative icons adjacent to text labels (like app icons in list items), cause redundant TalkBack announcements if they have a content description.
**Action:** Set `contentDescription = null` for these types of icons to prevent redundant announcements.
