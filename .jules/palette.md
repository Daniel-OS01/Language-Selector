
## 2023-10-27 - Remove redundant TalkBack announcements
**Learning:** In Jetpack Compose, icons or images inside components that have accompanying descriptive text, or components that inherently announce their state (like FilterChip), cause redundant double-announcements for screen reader users if `contentDescription` is also set.
**Action:** Always set `contentDescription = null` for icons and images when they are purely decorative or when their meaning is already conveyed by adjacent text or the parent component's inherent accessibility semantics.
