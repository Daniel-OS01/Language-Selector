## 2024-05-18 - Prevent Sensitive Data Extraction

**Vulnerability:** The application had `android:allowBackup="true"` and didn't explicitly exclude domains in data extraction rules, enabling users or attackers with physical access (or ADB) to extract sensitive application data, including shared preferences and databases, using device-to-device transfers or adb backup.

**Learning:** On Android 12+ (API 31+), `android:allowBackup="false"` is ignored for device-to-device transfers. To securely prevent backup and extraction of sensitive data, one must retain `android:dataExtractionRules` and explicitly `<exclude>` all domains (root, file, database, sharedpref, external) under both `<cloud-backup>` and `<device-transfer>` in `data_extraction_rules.xml`, and apply the same `<exclude>` rules under `<full-backup-content>` in `backup_rules.xml`.

**Prevention:** Always verify `android:allowBackup="false"` is set in `AndroidManifest.xml`, but more importantly, properly configure `data_extraction_rules.xml` and `backup_rules.xml` with explicit `<exclude>` rules for all domains to prevent extraction across all Android versions and backup/transfer methods.
