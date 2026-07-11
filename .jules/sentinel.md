## 2026-07-11 - Disabling Cleartext Traffic
**Vulnerability:** Cleartext (HTTP) traffic was potentially permitted by default in the application, exposing network communications to interception (Man-in-the-Middle attacks).
**Learning:** Modern Android versions restrict cleartext traffic by default, but it's crucial to explicitly declare this in a network security configuration to ensure consistency across all supported Android versions and prevent accidental regression or configuration overrides.
**Prevention:** Always explicitly define a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `AndroidManifest.xml` to strictly enforce HTTPS-only communication.
