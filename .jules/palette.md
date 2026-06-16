## 2024-05-24 - Accessibility Content Descriptions
**Learning:** Decorative icons adjacent to text should use `contentDescription = null` to avoid redundant TalkBack announcements. Also, localized strings via `stringResource(id = R.string.*)` should be used for functional icons instead of hardcoded English text.
**Action:** Audit and update contentDescription across Jetpack Compose components.
## 2024-05-24 - Clickable Semantic Roles
**Learning:** For Jetpack Compose `clickable` elements, set the appropriate semantic `role` (e.g., `Role.Button`). Only use the `onClickLabel` parameter if you can provide a descriptive verb (e.g., "open"). Avoid using nouns (like the button's text, e.g., "English") as the `onClickLabel` to prevent awkward TalkBack phrasing like "Double tap to English".
**Action:** Audit and update `clickable` modifiers to include `role = Role.Button` where appropriate.
## 2024-05-24 - Content Description Redundancy
**Learning:** Purely decorative icons or those accompanied by adjacent descriptive text (like an app icon next to the app name, or an icon in a labeled button) should use `contentDescription = null` to avoid redundant TalkBack announcements.
**Action:** Remove redundant `contentDescription`s in UI components like `QuickTextButton`, `AppListItem`, and `AboutScreen` where text already provides the necessary context.
