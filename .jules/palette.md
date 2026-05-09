## 2024-05-09 - Redundant TalkBack announcements on stateful components
**Learning:** Stateful Jetpack Compose components like `FilterChip` already announce their selected state, and icons accompanied by descriptive text cause TalkBack to redundantly read generic descriptions like "Done icon" or "app icon".
**Action:** Always set `contentDescription = null` for internal state-indicating icons and decorative icons next to text to ensure cleaner, non-repetitive screen reader announcements.
## 2024-05-09 - Accessible combined actions in LocaleItemList
**Learning:** In Jetpack Compose, elements using `combinedClickable` like `LocaleItemList` handle both tap and long-press interactions. However, without explicit labels (`onClickLabel`, `onLongClickLabel`), TalkBack uses generic announcements (e.g., "Double-tap to activate, Double-tap and hold to long press"), which doesn't convey the specific action (like pinning/unpinning a language).
**Action:** Provide meaningful accessibility action labels for `combinedClickable` elements.
