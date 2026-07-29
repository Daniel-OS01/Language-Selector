## 2024-04-05 - Accessibility in Jetpack Compose
**Learning:** This Android app using Jetpack Compose relies on TalkBack for accessibility. Many interactive UI components require proper `contentDescription` properties or semantic adjustments. I should find one place where the accessibility or user experience could be slightly improved.

**Action:** Review Jetpack Compose screens for `contentDescription` omissions on informative images or redundancies on decorative elements. I'll add content descriptions where they convey useful information to screen readers and set them to `null` where they're purely decorative or redundant.
