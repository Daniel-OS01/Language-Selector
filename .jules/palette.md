## 2024-05-18 - App Icons and TalkBack
**Learning:** In Jetpack Compose, purely decorative icons or those accompanied by adjacent descriptive text (e.g., an app icon next to the app name) should have `contentDescription = null` to avoid redundant TalkBack announcements. Also, functional icons that inherently manage their own accessibility state (like the 'Done' icon in a selected FilterChip) should have `contentDescription = null`.
**Action:** Set `contentDescription = null` on decorative/redundant icons and use the `role` parameter on `clickable` elements to improve screen reader experience.
