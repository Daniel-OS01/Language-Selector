## 2024-05-24 - Remove redundant contentDescriptions
**Learning:** Icons within components that inherently manage their own accessibility state (e.g., Done icon in FilterChip) or decorative icons adjacent to text labels (e.g., app icons in list items) shouldn't have `contentDescription` set. Doing so causes TalkBack to announce redundant information.
**Action:** Set `contentDescription = null` for decorative icons or icons in stateful accessible components to improve screen reader experience.
