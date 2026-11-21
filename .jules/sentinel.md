## 2024-11-10 - Disable Android Auto Backup
**Vulnerability:** Android application configured with `android:allowBackup="true"`.
**Learning:** `android:allowBackup="true"` allows a device's data to be backed up via adb or Google's cloud backup. If sensitive user data (or access tokens) are stored in plain text, they can be extracted by anyone with physical access and adb enabled, or restored to another device compromising the user's account. This repository handles potentially sensitive configurations that should not be automatically backed up.
**Prevention:** Set `android:allowBackup="false"` in `AndroidManifest.xml` unless specifically required and properly configured with strict backup rules.
