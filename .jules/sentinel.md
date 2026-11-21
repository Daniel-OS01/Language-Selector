## 2024-11-08 - Disable cleartext traffic via Network Security Config
**Vulnerability:** The Android application lacked a network_security_config.xml disabling cleartext traffic, creating a potential for unencrypted HTTP traffic (MITM risk).
**Learning:** In modern Android applications, network security requires an explicit opt-out for cleartext via networkSecurityConfig to enforce TLS/HTTPS strictly across all app components, since relying on system defaults can vary by API level and context.
**Prevention:** Always define and reference a strict network_security_config.xml in AndroidManifest.xml when initializing a new app or analyzing its security posture.
