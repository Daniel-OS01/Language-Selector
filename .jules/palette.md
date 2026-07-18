## 2024-07-18 - Redundant TalkBack Announcements in List Items
**Learning:** In the app's components (like AppListItem), providing a generic contentDescription ("app icon") for images adjacent to descriptive text labels within a single clickable parent causes redundant and noisy TalkBack announcements (e.g., "app icon, App Name").
**Action:** Set contentDescription to `null` for decorative icons in list items that are adjacent to descriptive text, and apply proper semantic roles (Role.Button) to the clickable parent.
