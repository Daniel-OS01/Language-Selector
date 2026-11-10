## 2024-11-10 - Redundant TalkBack Announcements for App Icons
**Learning:** In list items and screens where a decorative icon (like an app icon) is immediately paired with text representing the same information, providing a `contentDescription` on the image results in redundant and repetitive screen reader announcements (e.g., reading "app icon" followed by the app name).
**Action:** Always set `contentDescription = null` on these adjacent decorative icons in Jetpack Compose to streamline the TalkBack experience and reduce auditory clutter.
