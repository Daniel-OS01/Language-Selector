## 2024-07-26 - Disable cleartext HTTP traffic
**Vulnerability:** Android application allows cleartext HTTP traffic by default, making it vulnerable to interception and Man-In-The-Middle attacks.
**Learning:** Modern Android apps should explicitly disable cleartext HTTP traffic using `network_security_config.xml`.
**Prevention:** Always include a `network_security_config.xml` that sets `cleartextTrafficPermitted="false"` in the `<base-config>`.
