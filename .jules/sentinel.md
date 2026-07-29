## 2025-02-28 - Insecure Android Backup Configuration
**Vulnerability:** `android:allowBackup="true"` in AndroidManifest.xml.
**Learning:** This allows application data to be extracted over adb, posing a significant risk of data leakage if physical access is obtained or if a backup is maliciously restored.
**Prevention:** Set `android:allowBackup="false"` by default in new projects unless specifically required, and use specific backup rules if necessary.
