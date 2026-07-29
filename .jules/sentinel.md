## 2024-04-11 - Disable App Backup to prevent data extraction
**Vulnerability:** The application had `android:allowBackup="true"` in `AndroidManifest.xml`, allowing sensitive data to be backed up and potentially extracted using adb backup.
**Learning:** This is a common misconfiguration that could lead to sensitive data (like preferences, databases) being extracted off the device if a user's device is compromised or during a physical attack.
**Prevention:** Always set `android:allowBackup="false"` in `AndroidManifest.xml` unless there's a specific, secure need for backups. When doing so, remove `android:dataExtractionRules` and `android:fullBackupContent` to prevent useless configuration and lint warnings.
