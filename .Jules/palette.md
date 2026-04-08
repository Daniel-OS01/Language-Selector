## 2024-10-25 - Accessibility improvements for Compose Icons
**Learning:** TalkBack often reads both the icon `contentDescription` and the adjacent `Text` in Compose, creating redundant and annoying announcements (e.g., reading "Open" twice). Additionally, `clickable` elements default to generic "Double tap to activate" prompts.
**Action:** Set `contentDescription = null` on icons that are paired with visible descriptive text. Use `onClickLabel` in `.clickable()` to provide meaningful TalkBack interaction hints (e.g. "Double tap to Open" instead of "Double tap to activate").
