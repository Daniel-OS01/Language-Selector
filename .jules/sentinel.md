## 2024-05-16 - Prevent sensitive data extraction via device backups
**Vulnerability:** Application backups could expose sensitive data across devices or to the cloud, since backup control defaults (`allowBackup=true`) and default Android 12 extraction rules were left unchanged.
**Learning:** `android:allowBackup="false"` is ignored on Android 12+ for device-to-device transfers. Explicit exclusions are needed in `data_extraction_rules.xml` and `backup_rules.xml` for complete coverage.
**Prevention:** Always set `android:allowBackup="false"` AND explicitly define `<exclude>` rules for all domains in both `<cloud-backup>` and `<device-transfer>` tags within extraction rules, and under `<full-backup-content>` in backup rules.
