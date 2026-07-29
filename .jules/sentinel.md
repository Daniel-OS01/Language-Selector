## 2024-10-27 - Disable adb backup

**Vulnerability:** Android application configured with `android:allowBackup="true"`.
**Learning:** Setting `android:allowBackup="true"` allows sensitive application data extraction by users with USB access via `adb backup`. This creates a security risk where an attacker with physical access to an unlocked device could back up the app data.
**Prevention:** Explicitly set `android:allowBackup="false"` in AndroidManifest.xml for apps that handle sensitive user data, or unless absolutely necessary for standard application functions.
