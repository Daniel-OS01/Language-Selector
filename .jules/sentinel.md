## 2024-08-02 - Enforce strict network security
**Vulnerability:** Missing explicit network security configuration allowed cleartext (HTTP) traffic.
**Learning:** Modern Android applications should explicitly disable cleartext traffic via a network_security_config.xml to prevent unencrypted sensitive data transmission.
**Prevention:** Always create a network_security_config.xml with <base-config cleartextTrafficPermitted="false" /> and reference it in the AndroidManifest.xml.
