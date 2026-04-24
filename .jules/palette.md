## 2024-05-18 - Prevent redundant TalkBack announcements on paired Icon-Text elements
**Learning:** When `Icon` or `Image` elements are placed adjacently to `Text` labels or app names in Jetpack Compose layout trees (like `AppListItem` or `QuickTextButton`), TalkBack will announce both elements redundantly if the Icon has a `contentDescription`.
**Action:** Always set `contentDescription = null` on `Icon` or `Image` elements when their purpose is purely decorative or when they are strictly accompanied by an immediately adjacent descriptive text element.

## 2024-05-18 - Parameterize clickable modifier action labels
**Learning:** Reusing static text values (e.g. `text = "Settings"`) for `onClickLabel` in Compose `clickable` modifiers creates awkward phrasing ("Double tap to Settings"). Furthermore, relying on default accessibility labels provides a generic "Double tap to activate".
**Action:** Use contextual verb phrases (e.g. `Open %1$s` formatted with the app name, or `Select language` / `Pin language` for item selection) to provide descriptive actions for `onClickLabel` and `onLongClickLabel` to improve screen reader clarity.
