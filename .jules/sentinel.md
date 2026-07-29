## 2025-02-14 - Disabled `android:allowBackup` in AndroidManifest
**Vulnerability:** The application had `android:allowBackup="true"` in its `AndroidManifest.xml`.
**Learning:** This setting enables potential data leakage via `adb backup`, allowing extraction of the application's private data, which is especially risky for apps that manage sensitive settings or interact with privileged services like Shizuku.
**Prevention:** Always set `android:allowBackup="false"` in the manifest unless explicitly required and securely scoped.
