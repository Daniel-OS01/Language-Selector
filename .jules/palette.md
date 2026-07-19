## 2024-07-19 - Redundant TalkBack announcements in Compose Lists and Chips
**Learning:** In Jetpack Compose, setting a contentDescription on decorative icons (like AppListItem icons) or state-managing components (like the Done icon in FilterChip) causes TalkBack to announce redundant or overly noisy information, especially when adjacent text already conveys the meaning.
**Action:** Always set contentDescription to null for icons that are decorative or whose meaning is already provided by adjacent text or the parent component's semantic state.
