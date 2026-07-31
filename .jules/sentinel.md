## 2024-07-31 - Enforce HTTPS globally via Network Security Config
**Vulnerability:** Cleartext HTTP traffic was allowed by default in some configurations.
**Learning:** Explicitly disabling `cleartextTrafficPermitted` in `network_security_config.xml` ensures strict HTTPS-only transport, preventing man-in-the-middle data interception on Android apps.
**Prevention:** Always set up a network security config file blocking cleartext traffic for production applications.
