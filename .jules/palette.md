## 2024-04-23 - Jetpack Compose Accessibility Improvements
**Learning:** When using `clickable` or `combinedClickable` in Compose, setting `onClickLabel` and `onLongClickLabel` replaces the generic "Double tap to activate" announcement, making interactions much clearer. Also, setting `contentDescription = null` on icons adjacent to text prevents redundant screen reader announcements.
**Action:** Always provide descriptive interaction labels for touch targets and avoid redundant descriptions for decorative or text-paired icons.
