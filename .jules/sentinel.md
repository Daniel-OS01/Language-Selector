## 2024-07-16 - Prevent cleartext HTTP traffic
**Vulnerability:** The application was missing network security configuration, potentially allowing unencrypted cleartext HTTP traffic which is vulnerable to interception and man-in-the-middle attacks.
**Learning:** By default, older Android versions permit cleartext traffic, and relying on the default configuration is unsafe. It is critical to explicitly configure the application to deny cleartext traffic across all domains.
**Prevention:** Always create a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `AndroidManifest.xml` via the `android:networkSecurityConfig` attribute during initial application setup.
