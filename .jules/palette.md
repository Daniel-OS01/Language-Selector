## 2024-06-25 - Redundant TalkBack Announcements in Compose
**Learning:** In Jetpack Compose, when an `Icon` is accompanied by a descriptive `Text` element within an interactive container (like a clickable `Column` or `Row`), giving the `Icon` a `contentDescription` that matches the text causes screen readers (like TalkBack) to announce the label twice.
**Action:** Set `contentDescription = null` on icons that are purely decorative or when their meaning is already conveyed by adjacent text within the same interactive block.
