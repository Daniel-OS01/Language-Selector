## 2024-05-02 - Android 12+ Backup Vulnerability
**Vulnerability:** Application data could be backed up or transferred device-to-device, potentially exposing sensitive local state, databases, or preferences.
**Learning:** Setting `android:allowBackup="false"` in `AndroidManifest.xml` is insufficient to prevent device-to-device transfers on Android 12+ (API 31+).
**Prevention:** Always retain `android:dataExtractionRules` and `android:fullBackupContent` configurations in the manifest, and explicitly `<exclude>` all domains (`root`, `file`, `database`, `sharedpref`, `external`) within `data_extraction_rules.xml` (for both `<cloud-backup>` and `<device-transfer>`) and `backup_rules.xml` to fully secure application data from extraction.
