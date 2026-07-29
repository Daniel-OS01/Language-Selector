## 2026-06-19 - Android 12+ Backup Security Override
**Vulnerability:** Application backups could expose sensitive app data, even if `android:allowBackup="false"` is set, because device-to-device transfers ignore this flag on Android 12+.
**Learning:** To securely disable data extraction on modern Android versions, `data_extraction_rules.xml` must explicitly exclude all domains under both cloud-backup and device-transfer.
**Prevention:** Always implement explicit data extraction exclusions for all domains (root, file, database, sharedpref, external) when securing an app's backup behavior.
