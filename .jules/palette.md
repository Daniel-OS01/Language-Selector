## 2024-05-14 - Redundant TalkBack announcements for icons
**Learning:** Icons paired with descriptive text elements (like an app icon next to its name, or an icon in a text button) should have their `contentDescription` set to `null`. Providing a description causes screen readers to read the information twice, creating a frustrating experience.
**Action:** Always set `contentDescription = null` for purely decorative icons or those where adjacent text already conveys the meaning.
