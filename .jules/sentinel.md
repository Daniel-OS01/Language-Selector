## 2024-05-18 - Disable Data Extraction and Cloud Backups
**Vulnerability:** Android application explicitly allowed cloud and device-to-device backup/extraction, potentially exposing sensitive data.
**Learning:** `android:allowBackup="false"` in `AndroidManifest.xml` only prevents adb backup and older backup behaviors. For Android 12+ (API 31+), `android:dataExtractionRules` and `android:fullBackupContent` must also explicitly `<exclude>` domains to prevent data transfer during device migration.
**Prevention:** Always verify `allowBackup` is `false` and explicitly exclude domains in both `data_extraction_rules.xml` and `backup_rules.xml` to fully secure application data from device-to-device transfers.
