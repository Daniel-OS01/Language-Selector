## 2024-05-27 - Palette Journal Initialization
**Learning:** Initializing palette journal.
**Action:** Ready to record UX/a11y insights.
## 2024-05-27 - Jetpack Compose Redundant TalkBack Announcements
**Learning:** In Jetpack Compose, state-indicating components (like FilterChip's selected icon) and icons displayed directly next to/above their descriptive text will cause redundant TalkBack announcements if they also have a `contentDescription`.
**Action:** Always set `contentDescription = null` on icons that are either purely decorative, indicate inherent state of a parent component, or are immediately accompanied by readable descriptive text.
