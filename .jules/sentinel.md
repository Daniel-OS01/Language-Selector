## 2024-05-24 - Securely Disable Backups on Android 12+
**Vulnerability:** Application backups could be misused to extract sensitive app data.
**Learning:** Setting `android:allowBackup="false"` in the Manifest is ignored for device-to-device transfers on Android 12+ (API 31+).
**Prevention:** Must explicitly declare exclusion rules in `data_extraction_rules.xml` and `backup_rules.xml` for all storage domains to properly protect app data on newer OS versions.
