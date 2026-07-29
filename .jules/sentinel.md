## 2026-06-01 - Disable Android Backups Correctly
**Vulnerability:** Android backups were enabled and misconfigured, risking sensitive data extraction.
**Learning:** `android:allowBackup="false"` is insufficient on Android 12+. You must explicitly exclude domains in both `data_extraction_rules.xml` and `backup_rules.xml`.
**Prevention:** Ensure explicit exclusion rules are defined for cloud-backup, device-transfer, and full-backup-content when disabling backups.
