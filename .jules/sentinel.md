## 2024-11-09 - Disable Android Auto Backup
**Vulnerability:** Android auto-backup was enabled by default (android:allowBackup="true").
**Learning:** For apps with sensitive internal state or those communicating with root/Shizuku, allowing unencrypted backups via adb backup can leak sensitive state.
**Prevention:** Always set android:allowBackup="false" unless auto backup is strictly necessary and a custom backup rules XML is thoroughly reviewed.
