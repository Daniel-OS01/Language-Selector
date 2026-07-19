## 2024-07-19 - Enforce Strict Network Security
**Vulnerability:** Cleartext (HTTP) traffic was not explicitly disabled.
**Learning:** Modern Android applications should explicitly disable cleartext traffic via a network security config.
**Prevention:** Always create a network_security_config.xml with base-config cleartextTrafficPermitted="false" and reference it in the AndroidManifest.xml.
