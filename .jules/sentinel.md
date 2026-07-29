## 2024-07-03 - Enforce strict network security
**Vulnerability:** The application does not explicitly disable cleartext (HTTP) traffic, potentially allowing unencrypted data transmission depending on OS defaults.
**Learning:** The lack of a network_security_config.xml file means we rely on Android OS defaults which can vary by version and lead to implicit cleartext permission.
**Prevention:** Always create a network_security_config.xml to explicitly set cleartextTrafficPermitted="false" and reference it in the AndroidManifest.xml <application> tag.
