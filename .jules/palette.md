## 2024-06-22 - Redundant TalkBack Announcements in Components
**Learning:** Icons within self-managing components (like FilterChip's Done icon) or those accompanied by adjacent descriptive text (like an app icon next to its name) cause redundant TalkBack announcements if they have a contentDescription.
**Action:** Always set contentDescription = null for purely decorative icons or those whose state/meaning is inherently communicated by the parent component or adjacent text.
