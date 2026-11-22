## 2026-08-13 - Disable Cleartext Traffic
**Vulnerability:** Application allows cleartext (HTTP) traffic by default.
**Learning:** Modern Android applications should enforce strict network security by explicitly disabling cleartext traffic to prevent potential eavesdropping or man-in-the-middle attacks on unencrypted data.
**Prevention:** Always create a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the AndroidManifest.xml.
