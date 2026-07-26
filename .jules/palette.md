## 2024-07-26 - Redundant TalkBack announcements for decorative icons
**Learning:** In Jetpack Compose, icons within components that inherently manage their own accessibility state (such as the 'Done' icon in a selected FilterChip), or decorative icons adjacent to text labels (like app icons in list items), cause redundant TalkBack announcements if they have a contentDescription.
**Action:** Set `contentDescription = null` for icons that are decorative or whose state is already communicated by the parent component.
