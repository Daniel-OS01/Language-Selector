## 2024-05-24 - Device-to-Device Backup Extraction on Android 12+
**Vulnerability:** The app allowed data extraction during device-to-device transfers because `android:allowBackup="false"` is ignored for transfers on Android 12+ (API 31+).
**Learning:** To securely disable backups and transfers on Android 12+, explicitly configuring `<exclude>` rules for all domains (`root`, `file`, `database`, `sharedpref`, `external`) under both `<cloud-backup>` and `<device-transfer>` in `data_extraction_rules.xml` (and `<full-backup-content>` in `backup_rules.xml`) is required.
**Prevention:** Always verify `data_extraction_rules.xml` and `backup_rules.xml` include explicit exclusion rules rather than relying solely on `android:allowBackup="false"`.
