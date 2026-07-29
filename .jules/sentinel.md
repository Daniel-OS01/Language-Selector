## 2024-05-18 - Enforce Secure Network Traffic
**Vulnerability:** Application allowed cleartext (HTTP) traffic by default.
**Learning:** Modern Android apps should explicitly disable cleartext traffic to prevent man-in-the-middle attacks and data interception.
**Prevention:** Always create a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `AndroidManifest.xml`.
