## 2024-08-01 - Disable cleartext traffic
**Vulnerability:** The application allowed cleartext HTTP traffic by default, which can expose data to interception.
**Learning:** Modern Android applications should explicitly disable cleartext traffic to ensure all network communication uses HTTPS.
**Prevention:** Always create a network_security_config.xml with <base-config cleartextTrafficPermitted="false" /> and reference it in AndroidManifest.xml.
