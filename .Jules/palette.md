## 2026-04-20 - Optimize Jetpack Compose Screen Reader Experience
**Learning:** In Jetpack Compose, having a generic `contentDescription` (e.g., 'App icon') right next to textual components that identify the object causes redundant and annoying TalkBack announcements (e.g., reading 'App icon' right before reading the actual app's name).
**Action:** Set `contentDescription = null` for decorative icons or images that are immediately accompanied by descriptive text to reduce auditory clutter for screen reader users.
