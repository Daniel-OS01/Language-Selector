## 2024-05-24 - [Disable allowBackup in AndroidManifest.xml]
**Vulnerability:** allowBackup="true" in AndroidManifest.xml
**Learning:** This exposes the app to data leakage via ADB backup, allowing an attacker with physical access or a malicious app on the same device to extract sensitive app data.
**Prevention:** Always set android:allowBackup="false" unless explicit backup functionality is required, and in that case, restrict it appropriately.
