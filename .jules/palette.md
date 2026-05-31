## 2024-05-24 - Accessibility Improvements for Icons
**Learning:** Components that announce their state or have adjacent text don't need content descriptions for their internal icons, and generic "Double tap to activate" on clickables can be improved with onClickLabel and Role.
**Action:** Set contentDescription = null for decorative/state icons and use onClickLabel and role on clickables.
