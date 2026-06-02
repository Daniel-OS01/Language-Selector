## 2024-06-02 - Improved Talkback handling in AppListItem
**Learning:** Generic `clickable` announcements on Android app list items ("Double tap to activate") provide little context. The `onClickLabel` with a specific role improves accessibility for list items drastically. Decorative icons without `contentDescription = null` add redundant speech context.
**Action:** Always provide descriptive `onClickLabel` using string resources and set descriptive roles like `Role.Button` to `clickable`. Set decorative icons' `contentDescription` to null if there is accompanying text.
