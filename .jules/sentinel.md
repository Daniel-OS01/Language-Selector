## 2024-03-30 - Disable Android App Backup
**Vulnerability:** Android application backup feature was enabled (`android:allowBackup="true"`), potentially allowing extraction of sensitive app data via ADB.
**Learning:** `android:allowBackup="true"` allows data extraction via `adb backup`, which can be a risk for applications that handle sensitive settings or permissions.
**Prevention:** Always set `android:allowBackup="false"` in the `AndroidManifest.xml` unless specifically required and user data is explicitly excluded.
