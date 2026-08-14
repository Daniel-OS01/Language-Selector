## 2024-08-14 - Fix redundant TalkBack announcements in Compose items
**Learning:** In Jetpack Compose, icons within components that inherently manage their own accessibility state (like `FilterChip`), or decorative icons adjacent to text labels (like app icons in list items), cause redundant TalkBack announcements if `contentDescription` is explicitly set to a string.
**Action:** Set `contentDescription = null` for decorative icons and icons within self-managing accessibility components, unless they convey a unique critical state (e.g., 'Pinned') not present in adjacent text.
