## 2026-08-05 - Enforce Strict Network Security
**Vulnerability:** The Android application did not explicitly disable cleartext (HTTP) traffic.
**Learning:** Modern Android applications should explicitly disable cleartext traffic via a network security configuration to prevent accidental unencrypted network communications and protect against man-in-the-middle attacks.
**Prevention:** Always explicitly define a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `AndroidManifest.xml` `<application>` tag.
