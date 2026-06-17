## 2024-05-20 - Redundant TalkBack announcements for decorative icons
**Learning:** Purely decorative icons or icons adjacent to descriptive text (like an app icon next to the app name) should not have a contentDescription because it causes redundant and unnecessary TalkBack announcements.
**Action:** Set contentDescription = null for icons that are accompanied by adjacent descriptive text.
