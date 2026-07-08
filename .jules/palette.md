## 2023-10-27 - Remove redundant screen reader announcements
**Learning:** In Jetpack Compose, icons within components that manage their own accessibility state (like FilterChip or when paired with clear text) often result in redundant TalkBack announcements if they have content descriptions.
**Action:** Set `contentDescription = null` for functional or decorative icons that duplicate information already conveyed by surrounding text or the component's inherent state.
