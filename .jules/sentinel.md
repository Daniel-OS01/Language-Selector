## 2024-05-25 - Securely Disable Android App Backups
**Vulnerability:** The application had `android:allowBackup="true"`, allowing sensitive app data to be extracted via backup or device-to-device transfer. Setting it to `false` alone is insufficient on Android 12+.
**Learning:** On Android 12+ (API 31+), `allowBackup="false"` is ignored for device-to-device transfers. Complete mitigation requires `dataExtractionRules` and `fullBackupContent` to explicitly exclude all storage domains.
**Prevention:** Always set `android:allowBackup="false"` and define strict `<exclude>` rules for all domains (`root`, `file`, `database`, `sharedpref`, `external`) in `data_extraction_rules.xml` and `backup_rules.xml`.
