## 2026-07-01 - Redundant TalkBack announcements in FilterChip
**Learning:** In Jetpack Compose, icons within components that inherently manage their own accessibility state (such as the 'Done' icon in a selected FilterChip) cause redundant TalkBack announcements if they have a content description.
**Action:** Always set `contentDescription = null` for state-indicating icons inside parent components like FilterChip that already announce their state.
