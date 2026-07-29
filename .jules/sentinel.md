## 2024-11-20 - [Disable App Backup to Prevent Data Leakage]
**Vulnerability:** The application's `AndroidManifest.xml` had `android:allowBackup="true"`.
**Learning:** Having `android:allowBackup="true"` means the app's data (including databases and preferences) is backed up to the user's Google account or via ADB. Since this app requires Root/Shizuku and manages sensitive locale settings, the user might not want the Shizuku configurations, internal databases (like AppInfoDB), and preferences to be automatically backed up.
**Prevention:** To prevent this potential data exposure, `android:allowBackup="false"` should be explicitly set, especially for apps that rely heavily on elevated privileges or store sensitive configuration data.
