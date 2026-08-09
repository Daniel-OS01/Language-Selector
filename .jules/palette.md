## 2024-08-09 - Fix redundant TalkBack announcements and missing semantic roles
**Learning:** TalkBack redundantly announces decorative icons and icons accompanying descriptive text, causing poor UX. Clickable modifiers without semantic roles fail to announce interaction intent to screen readers.
**Action:** Set `contentDescription = null` for purely decorative or adjacently described icons. Always explicitly assign semantic roles (e.g., `role = androidx.compose.ui.semantics.Role.Button`) to custom `.clickable` components.
