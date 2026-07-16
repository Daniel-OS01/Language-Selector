## 2024-05-18 - Redundant TalkBack announcements for decorative icons
**Learning:** Icons within components that manage their own accessibility state (like the 'Done' icon in a selected FilterChip) or decorative icons adjacent to descriptive text labels (like app icons in list items) should have their `contentDescription` set to `null` to prevent redundant TalkBack announcements.
**Action:** Set `contentDescription = null` for purely decorative icons and icons inside inherently accessible components.
