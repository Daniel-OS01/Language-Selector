## 2024-07-10 - Enforce secure network traffic by explicitly disabling cleartext communication
**Vulnerability:** The application did not explicitly forbid cleartext (HTTP) network traffic.
**Learning:** By default on older Android versions or if misconfigured, Android applications can communicate over unencrypted HTTP connections, exposing sensitive data to eavesdropping or man-in-the-middle attacks.
**Prevention:** Always create a `network_security_config.xml` file with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `AndroidManifest.xml` via `android:networkSecurityConfig="@xml/network_security_config"` to enforce HTTPS usage application-wide.
