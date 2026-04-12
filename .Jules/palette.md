## 2024-04-12 - Prevent redundant TalkBack announcements
**Learning:** In Jetpack Compose, setting `contentDescription` on an `Icon` inside a `clickable` container that already has descriptive `Text` causes TalkBack to read the text twice. Also, `clickable` elements without `onClickLabel` announce generically ("Double tap to activate").
**Action:** Always set `contentDescription = null` for icons accompanied by text, and use `onClickLabel` on the parent `clickable` to provide actionable screen reader context.
