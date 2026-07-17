## 2024-05-18 - Missing Network Security Config
**Vulnerability:** The application lacks a network security configuration to explicitly disable cleartext (HTTP) traffic.
**Learning:** By default, older Android versions or misconfigurations might allow unencrypted HTTP traffic, posing a risk of eavesdropping or man-in-the-middle attacks.
**Prevention:** Always include a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `AndroidManifest.xml`.
