## 2024-05-18 - Avoid redundant TalkBack descriptions on icons with text
**Learning:** When using Jetpack Compose, icons that are accompanied by descriptive text should have `contentDescription = null`. Setting the `contentDescription` on the icon to the same text as the accompanying label causes screen readers (like TalkBack) to announce the text redundantly (e.g., "Settings, Settings").
**Action:** Always verify if an icon is decorative or accompanied by text before adding a `contentDescription`. If accompanied by text, use `null`.

## 2024-05-18 - Enhance `clickable` elements with `onClickLabel`
**Learning:** By default, Jetpack Compose's `clickable` modifier announces "Double tap to activate" on screen readers. This can be vague.
**Action:** Use the `onClickLabel` parameter in the `clickable` modifier to provide a more descriptive action (e.g., `clickable(onClickLabel = stringResource(id = R.string.open))`) so TalkBack announces "Double tap to open" instead.