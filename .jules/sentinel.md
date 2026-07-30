## 2024-05-24 - Explicitly disable cleartext traffic
**Vulnerability:** The application was missing an explicit configuration to disable cleartext (HTTP) traffic, which could allow unencrypted data transmission if a developer accidentally makes an HTTP request instead of HTTPS.
**Learning:** Modern Android apps should default to secure connections by explicitly disabling cleartext traffic in a network security configuration file. Relying on default behavior or ad-hoc HTTPS usage is insufficient for defense-in-depth.
**Prevention:** Always define a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and link it in the `AndroidManifest.xml`'s `<application>` tag for all new projects.
