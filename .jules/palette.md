## 2024-07-04 - Remove redundant content descriptions in FilterChip
**Learning:** Icons within components that inherently manage their own accessibility state (such as the 'Done' icon in a selected FilterChip) cause redundant TalkBack announcements if they have a content description.
**Action:** Set `contentDescription = null` for icons in such components.
