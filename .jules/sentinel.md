## 2025-02-14 - Disable cleartext traffic in Android App
**Vulnerability:** The application was implicitly permitting cleartext (HTTP) traffic, which allows man-in-the-middle attacks where network traffic could be intercepted and modified.
**Learning:** Android modern standards require apps to opt out of cleartext traffic explicitly. The app had no network security configuration defined.
**Prevention:** Always explicitly define a `network_security_config.xml` with cleartext traffic disabled and apply it to the `<application>` tag in `AndroidManifest.xml`.
