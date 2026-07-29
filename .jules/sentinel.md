## 2024-06-18 - Securely disabling Android backups
**Vulnerability:** Android backups and device-to-device transfers could inadvertently extract sensitive application data if not properly configured.
**Learning:** Setting `android:allowBackup="false"` is insufficient on Android 12+ (API 31+). You must retain `android:dataExtractionRules` and explicitly `<exclude>` all domains (root, file, database, sharedpref, external) under both `<cloud-backup>` and `<device-transfer>` in `data_extraction_rules.xml`, and apply the same `<exclude>` rules under `<full-backup-content>` in `backup_rules.xml`.
**Prevention:** Always use XML configuration to explicitly define excluded domains and paths for both backups and device transfers on Android 12+.
