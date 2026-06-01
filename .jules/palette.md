## 2026-06-01 - Redundant TalkBack announcements
**Learning:** Decorative icons next to text or within inherently stateful components (like FilterChip) create redundant TalkBack announcements if they have a contentDescription.
**Action:** Always set contentDescription = null for decorative or state-indicating icons and use semantic roles/labels on modifiers.
