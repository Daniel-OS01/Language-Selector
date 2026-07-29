## 2024-05-18 - Enforce strict network security in Android
**Vulnerability:** Cleartext (HTTP) traffic is potentially permitted by default in older Android versions and without explicit configuration.
**Learning:** Modern Android apps should explicitly disable cleartext traffic to enforce secure network communication and prevent man-in-the-middle attacks. This is done by creating a `network_security_config.xml` with `<domain-config cleartextTrafficPermitted="false">` and referencing it in the `<application>` tag of `AndroidManifest.xml`.
**Prevention:** Always include a strict `network_security_config.xml` and enforce HTTPS-only communication.
