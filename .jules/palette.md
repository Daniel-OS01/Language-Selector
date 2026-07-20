## 2024-07-20 - Palette Initialization
**Learning:** Initializing palette journal.
**Action:** Use this file to log critical UX/a11y learnings.
## 2024-07-20 - Redundant App Icon Announcements
**Learning:** Found that `Image` components inside `AppListItem`, `AboutScreen`, and `AppInfoScreen` representing app icons have descriptive string `contentDescription = "App icon"` or `"app icon"`. However, because they are decorative or contextually obvious next to the app name/package text (or in a clickable row where the whole row handles the action), screen readers will redundantly read "App icon" when navigating. In list items like `AppListItem` or list-like UI, decorative adjacent icons should have `contentDescription = null` to prevent redundant TalkBack phrasing.
**Action:** Always set `contentDescription = null` on decorative icons or icons adjacent to explicit text labels that provide the full context, especially inside clickable components or list items.
## 2024-07-20 - Missing Semantic Role in Clickable Items
**Learning:** `clickable` and `combinedClickable` modifiers in components like `AppListItem`, `QuickTextButton`, and `LocaleItemList` are missing a semantic `role` mapping. This causes screen readers like TalkBack to not announce these generic elements as "buttons" or interactable items, confusing users. For `QuickTextButton` which visually functions as a button, missing `role = Role.Button` is an accessibility gap.
**Action:** When creating custom clickable elements using `clickable` or `combinedClickable`, explicitly provide an appropriate accessibility `role` (e.g., `role = androidx.compose.ui.semantics.Role.Button`) so screen readers accurately describe the element's interaction model.
