## 2024-07-20 - Disable cleartext HTTP traffic
**Vulnerability:** The application permits cleartext HTTP traffic by default.
**Learning:** Android applications need explicit network security configurations to prevent accidental unencrypted data transmission.
**Prevention:** Always include a network_security_config.xml disabling cleartext traffic for strict network security.
