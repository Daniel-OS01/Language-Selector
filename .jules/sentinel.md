## 2024-07-05 - Enforce strict network security by disabling cleartext traffic
**Vulnerability:** Cleartext (HTTP) traffic was not explicitly disabled in the Android application, potentially allowing sensitive data to be transmitted unencrypted.
**Learning:** Modern Android applications should explicitly disable cleartext traffic via a network security config to enforce HTTPS and protect against man-in-the-middle attacks.
**Prevention:** Always create a network_security_config.xml with <base-config cleartextTrafficPermitted="false" /> and reference it in AndroidManifest.xml.
