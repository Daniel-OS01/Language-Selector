## 2024-05-24 - [Disable App Backup to Prevent Sensitive Data Extraction]
**Vulnerability:** The application had `android:allowBackup="true"` set in its `AndroidManifest.xml`, meaning that by default, the app's internal storage and SharedPreferences could be backed up.
**Learning:** For security reasons, sensitive app data can be extracted using the adb backup command and this vulnerability should be disabled unless the functionality to backup and restore the app's contents is explicitly required and correctly implemented to exclude sensitive user data.
**Prevention:** Always default to setting `android:allowBackup="false"` in Android apps. Make sure to remove unused and incompatible `android:dataExtractionRules` and `android:fullBackupContent` when doing so.
