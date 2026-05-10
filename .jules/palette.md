## 2024-06-25 - Redundant TalkBack announcements on state-indicating icons
**Learning:** In Jetpack Compose, components that inherently announce their state (like `FilterChip` for selection) cause redundant TalkBack announcements if their internal state-indicating icons (e.g., a checkmark) also have a `contentDescription`.
**Action:** Set `contentDescription = null` for internal state-indicating icons on components that already announce their state, such as `FilterChip`.
