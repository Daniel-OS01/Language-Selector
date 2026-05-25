## 2026-05-25 - Prevent Redundant Screen Reader Announcements
**Learning:** Icons paired with descriptive text or within state-indicating components (like FilterChip) should have `contentDescription = null` to prevent TalkBack from reading redundant information.
**Action:** Always verify if an icon's context provides sufficient description before adding a `contentDescription`.
