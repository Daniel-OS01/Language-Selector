## 2024-10-24 - Enforce strict network security configuration
**Vulnerability:** Application allows cleartext traffic and lacks a strict network security policy, potentially exposing sensitive data to interception (e.g., Man-in-the-Middle attacks).
**Learning:** Modern Android applications should explicitly disable cleartext (HTTP) traffic to ensure all network communications are encrypted (HTTPS).
**Prevention:** Implement a `network_security_config.xml` that sets `cleartextTrafficPermitted="false"` globally, ensuring all network calls use secure transport.
