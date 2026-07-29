## 2024-10-31 - Disabled Android Backup Data Extraction
**Vulnerability:** The application had `android:allowBackup="true"` enabled in `AndroidManifest.xml` and missing `<exclude>` rules in backup/extraction XML configurations.
**Learning:** `android:allowBackup="false"` alone is insufficient for Android 12+ devices, as it is ignored during device-to-device transfers. All domains (root, file, database, sharedpref, external) must be explicitly excluded in both `<cloud-backup>` and `<device-transfer>` inside `data_extraction_rules.xml`, as well as in `backup_rules.xml` for full backwards compatibility.
**Prevention:** Ensure explicit domain exclusions exist in extraction and backup rule XML files rather than relying solely on `android:allowBackup`.
