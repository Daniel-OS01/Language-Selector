## 2026-06-16 - Securely Disable Data Extraction
**Vulnerability:** Android application explicitly allowed backups and device-to-device transfers, risking sensitive data extraction.
**Learning:** On Android 12+ (API 31+), `android:allowBackup="false"` is ignored for device-to-device transfers. Complete prevention requires explicit exclusion rules in `data_extraction_rules.xml` and `backup_rules.xml` for all domains.
**Prevention:** Always maintain `data_extraction_rules.xml` with explicit `<exclude>` configurations for `root`, `file`, `database`, `sharedpref`, and `external` domains to securely disable backups.
