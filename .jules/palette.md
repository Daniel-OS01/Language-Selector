## 2024-10-25 - Prevent redundant TalkBack announcements
**Learning:** Icons within components that inherently manage their own accessibility state (such as the 'Done' icon in a selected FilterChip), or decorative icons adjacent to text labels (like app icons in list items), cause redundant and confusing TalkBack announcements if they have explicit content descriptions.
**Action:** Set `contentDescription = null` for decorative icons and icons in state-managed components to prevent redundant screen reader announcements.
