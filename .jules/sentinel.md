## 2024-05-18 - Disable Cleartext Traffic
**Vulnerability:** The Android application allowed cleartext (HTTP) traffic by default, which can lead to man-in-the-middle (MitM) attacks where sensitive data is intercepted or manipulated over unencrypted networks.
**Learning:** Modern Android apps should enforce HTTPS for all communications. Even if the app doesn't currently make HTTP requests, configuring this at the OS level provides defense-in-depth and prevents future accidental regressions.
**Prevention:** Always create a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `AndroidManifest.xml` via `android:networkSecurityConfig` for all new Android projects.
