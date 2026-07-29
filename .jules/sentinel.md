## 2025-05-14 - Prevent unintended data extraction via Android 12+ backup rules
**Vulnerability:** Android 12+ (API 31+) ignores `android:allowBackup="false"` for device-to-device transfers, potentially allowing sensitive data extraction.
**Learning:** `android:allowBackup="false"` is insufficient to prevent all backups/transfers. You must explicitly configure `data_extraction_rules.xml` and `backup_rules.xml`.
**Prevention:** Retain `android:dataExtractionRules` and explicitly `<exclude>` all domains (`root`, `file`, `database`, `sharedpref`, `external`) under both `<cloud-backup>` and `<device-transfer>` in `data_extraction_rules.xml`, and apply the same `<exclude>` rules under `<full-backup-content>` in `backup_rules.xml`.
