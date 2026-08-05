## 2024-05-24 - Fix redundant TalkBack announcements
**Learning:** In Jetpack Compose, icons within components that inherently manage their own accessibility state (such as the 'Done' icon in a selected FilterChip), or decorative icons adjacent to text labels (like app icons in list items), cause redundant TalkBack announcements when a description is provided.
**Action:** Set contentDescription to null for these icons to prevent redundant TalkBack announcements.
