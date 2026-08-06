## 2024-08-06 - Add explicit Semantic Role to clickable components in Jetpack Compose
**Learning:** Custom clickable components in Jetpack Compose do not automatically announce themselves as buttons to screen readers, missing out on important accessibility cues.
**Action:** When applying `.clickable` modifiers to custom composable items that function like buttons, explicitly specify a `role = androidx.compose.ui.semantics.Role.Button` to ensure screen readers announce them properly.
