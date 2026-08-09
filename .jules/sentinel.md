## 2024-12-31 - Enforce Strict Network Security
**Vulnerability:** Cleartext (HTTP) traffic was not explicitly disabled.
**Learning:** Modern Android applications should explicitly disable cleartext traffic via a network security config, ensuring no unencrypted data is accidentally transmitted over the network.
**Prevention:** Always define a network-security-config with cleartextTrafficPermitted="false" and reference it in the application tag of AndroidManifest.xml.
