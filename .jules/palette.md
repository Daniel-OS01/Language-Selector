## 2024-07-12 - Prevent redundant TalkBack announcements and improve click semantics
**Learning:** In Jetpack Compose, decorative icons (like app icons in list items) or icons in components that manage their own state (like FilterChip's 'Done') cause redundant TalkBack announcements if they have a `contentDescription`. Additionally, interactive modifiers like `clickable` require explicit semantic roles to announce correctly.
**Action:** Set `contentDescription = null` for non-functional/state-managed icons, and assign `Role.Button` to clickables to improve screen reader clarity.
