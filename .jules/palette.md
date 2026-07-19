## 2024-07-06 - Redundant TalkBack Announcements in Jetpack Compose
**Learning:** In Jetpack Compose, icons within components that inherently manage their own accessibility state (such as the 'Done' icon in a selected FilterChip) should have `contentDescription = null` to prevent redundant TalkBack announcements.
**Action:** When adding icons to inherently stateful Compose components (like checkboxes, switches, or chips), evaluate if the component itself already conveys the necessary state to screen readers. If so, set the icon's `contentDescription` to `null`.
