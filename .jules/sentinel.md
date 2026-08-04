## 2025-02-12 - Missing Network Security Config
**Vulnerability:** The application lacks a `network_security_config.xml` to explicitly disable cleartext (HTTP) traffic, which is a modern Android security requirement.
**Learning:** Even if the app doesn't currently use HTTP, the missing configuration leaves it vulnerable if external libraries or future features attempt unencrypted communication. The platform default allows cleartext on older Android versions, so explicit configuration is needed for consistent security.
**Prevention:** Always add a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` and reference it in the `AndroidManifest.xml` during project initialization.
