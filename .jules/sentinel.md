## 2024-05-18 - Disabling Android Backups Securely
**Vulnerability:** Application backups could be extracted device-to-device despite `android:allowBackup="false"` because device-transfer rules were not explicitly defined in `data_extraction_rules.xml`.
**Learning:** `android:allowBackup="false"` is ignored on Android 12+ (API 31+) for device-to-device transfers. Explicitly defining `<device-transfer>` exclusions with specific domains and paths is required to completely block data extraction.
**Prevention:** Always pair `android:allowBackup="false"` with comprehensive XML rules defining `<exclude domain="..." path="."/>` for all domains (`root`, `file`, `database`, `sharedpref`, `external`) in both `data_extraction_rules.xml` and `backup_rules.xml`.
