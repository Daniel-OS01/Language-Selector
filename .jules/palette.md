## 2024-06-08 - Compose Semantics Optimization
**Learning:** Avoid redundant `contentDescription` on decorative icons inside complex interactive components like `AppListItem` or stateful elements like `FilterChip`. Use `role` and `onClickLabel` on modifiers like `clickable` to give concise, context-aware TalkBack feedback.
**Action:** Always set `contentDescription = null` for strictly decorative icons, relying on parent semantic properties.
