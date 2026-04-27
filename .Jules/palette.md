## 2024-05-01 - Redundant TalkBack Announcements in Jetpack Compose
**Learning:** When icons (`Image` or `Icon` composables) are paired with descriptive `Text` composables within the same parent or are immediately followed by them, providing a `contentDescription` for the icon causes TalkBack to read the description redundantly.
**Action:** Set `contentDescription = null` on icons that are accompanied by descriptive text to avoid redundant TalkBack announcements and improve the screen reader experience.
