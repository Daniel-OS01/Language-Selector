## 2024-07-08 - Explicitly disable cleartext traffic
**Vulnerability:** Application allowed cleartext HTTP traffic, which can expose data to interception.
**Learning:** Modern Android apps should enforce strict network security by disabling cleartext traffic at the application level.
**Prevention:** Always create a `network_security_config.xml` explicitly setting `cleartextTrafficPermitted="false"` and reference it in the `AndroidManifest.xml` via `android:networkSecurityConfig`.
