## 2026-08-12 - Disable cleartext traffic
**Vulnerability:** The application did not explicitly disable cleartext (HTTP) traffic.
**Learning:** Modern Android applications should enforce strict network security by disabling cleartext traffic at the application level using a network security configuration.
**Prevention:** Always create a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `<application>` tag of `AndroidManifest.xml` during project setup.
