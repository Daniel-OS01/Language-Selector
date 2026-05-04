## 2024-05-24 - Prevent Redundant TalkBack Announcements
**Learning:** In Jetpack Compose, icons accompanied by descriptive text or internal to state-indicating components (like FilterChip) cause redundant TalkBack announcements if they have a content description.
**Action:** Set `contentDescription = null` on decorative icons or icons alongside text.
