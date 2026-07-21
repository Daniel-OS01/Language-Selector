## 2025-07-21 - Enforce strict network security by disabling cleartext traffic
**Vulnerability:** The application was missing a network security configuration to explicitly disable cleartext (HTTP) traffic.
**Learning:** By default on older API levels, cleartext traffic might be permitted, which can expose the application to Man-in-the-Middle (MitM) attacks where an attacker could intercept and read sensitive data transmitted over the network.
**Prevention:** Always create a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `<application>` tag of `AndroidManifest.xml` to enforce secure connections.
