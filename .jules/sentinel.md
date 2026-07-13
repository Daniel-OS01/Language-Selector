## 2024-07-13 - Enforce strict network security by disabling cleartext traffic
**Vulnerability:** The application lacked explicit disablement of cleartext traffic in AndroidManifest, relying on default OS behaviors.
**Learning:** Modern Android applications like this one must explicitly disable cleartext traffic via a network security config, ensuring all communications are forcefully encrypted (HTTPS) to prevent interception.
**Prevention:** Always define a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the AndroidManifest.xml's `<application>` tag.
