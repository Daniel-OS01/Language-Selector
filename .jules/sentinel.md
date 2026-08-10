## 2025-02-18 - Enforce strict network security by disabling cleartext traffic
**Vulnerability:** The application was lacking explicit configuration to disable cleartext (HTTP) traffic.
**Learning:** In modern Android applications, explicitly disabling cleartext traffic at the application level via `network_security_config.xml` is an important pattern to prevent unencrypted sensitive data transmission.
**Prevention:** Always define a network security configuration and enforce strict HTTPS policies for all communication, avoiding default fallback to cleartext.
