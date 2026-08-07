## 2024-08-07 - Fix redundant TalkBack announcements
**Learning:** In Jetpack Compose, decorative icons adjacent to text labels (like app icons) and icons inside stateful components (like `FilterChip`) get announced redundantly by TalkBack if they have a non-null `contentDescription`.
**Action:** Always set `contentDescription = null` for purely decorative icons or icons whose state is already announced by their parent component.
