## 2024-06-18 - Jetpack Compose FilterChip accessibility

**Learning:** When using `FilterChip` in Jetpack Compose, the component inherently announces its selected state via TalkBack. Any internal state-indicating icons, such as a checkmark when selected, should have their `contentDescription` set to `null`. Providing a description like "Done icon" causes redundant and confusing screen reader announcements.

**Action:** Always set `contentDescription = null` for internal state-indicating icons within components like `FilterChip`, `Checkbox`, or `RadioButton` that already manage their own accessibility state announcements.
