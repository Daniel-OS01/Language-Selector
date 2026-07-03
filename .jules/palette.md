## 2024-07-03 - [Remove redundant TalkBack announcements in FilterChip]
**Learning:** In Jetpack Compose, icons within components that inherently manage their own accessibility state (like the "Done" icon in a selected `FilterChip`) will cause redundant TalkBack announcements if they have a non-null `contentDescription`. The screen reader already announces the selected state of the chip itself.
**Action:** When adding icons to inherently stateful Compose components, set `contentDescription = null` to prevent double-announcing the state.
