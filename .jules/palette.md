## 2024-05-24 - Redundant TalkBack announcements for decorative/state-managed icons
**Learning:** Hardcoded accessibility descriptions (like "app icon" or "Done icon") on purely decorative icons next to text, or icons within state-managed components (like FilterChip), cause redundant and annoying TalkBack announcements.
**Action:** Always set `contentDescription = null` for purely decorative icons accompanied by text and icons in inherently accessible state-managed components to streamline screen reader navigation.
