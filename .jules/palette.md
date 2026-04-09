## 2024-04-09 - Jetpack Compose TalkBack Optimizations
**Learning:** In Jetpack Compose, icons placed adjacent to descriptive text cause redundant TalkBack announcements if they both have content descriptions. Additionally, generic `clickable` modifiers result in unhelpful "Double tap to activate" announcements.
**Action:** Always set `contentDescription = null` on decorative icons or icons accompanied by text labels. Use the `onClickLabel` parameter on `Modifier.clickable()` to replace generic actions with descriptive ones (e.g., "Double tap to open").
