## 2026-07-27 - Disable Cleartext HTTP Traffic
**Vulnerability:** The application did not explicitly disable cleartext (HTTP) traffic at the OS level.
**Learning:** Modern Android apps should enforce strict network security by disabling cleartext traffic using `network_security_config.xml` to prevent unencrypted data transmission by any component or library.
**Prevention:** Always create a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `AndroidManifest.xml` during app initialization.
