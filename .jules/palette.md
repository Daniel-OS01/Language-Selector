## 2024-06-20 - Redundant TalkBack Announcements on Compound Buttons
**Learning:** Compound components like AppListItem or QuickTextButton with both an icon/image and text cause screen readers (TalkBack) to read redundant information if both are labeled.
**Action:** Set contentDescription = null on the icon/image and ensure the interactive container has role = Role.Button to announce properly without repeating text.
