## 2024-07-28 - Disable cleartext HTTP traffic
**Vulnerability:** The application does not explicitly disable cleartext (HTTP) traffic, which could expose sensitive data in transit to interception.
**Learning:** To enforce strict network security in modern Android applications, we must explicitly disable cleartext (HTTP) traffic via network_security_config.xml.
**Prevention:** Always create a network_security_config.xml with `<base-config cleartextTrafficPermitted="false" />` and reference it in AndroidManifest.xml.
