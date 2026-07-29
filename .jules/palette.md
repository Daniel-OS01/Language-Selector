## 2025-01-20 - Nullifying Content Descriptions for Managed State Icons
**Learning:** Icons within components that inherently manage their own accessibility state (such as the 'Done' icon in a selected FilterChip) should have `contentDescription = null` to prevent redundant TalkBack announcements.
**Action:** When using Material components like FilterChip, set the contentDescription of state-indicating icons to null.
