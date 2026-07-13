## 2023-10-27 - Fix redundant TalkBack announcements
**Learning:** Icons within components that inherently manage their own accessibility state (like the 'Done' icon in a selected FilterChip), or decorative icons adjacent to text labels in a clickable container (like app icons in list items), cause redundant TalkBack announcements if they have a `contentDescription`.
**Action:** Set `contentDescription = null` for decorative icons adjacent to text and icons in state-managed components to prevent duplicate read-outs.
