## 2026-08-03 - Disable Cleartext Traffic
**Vulnerability:** Cleartext HTTP traffic was permitted globally by default, risking data interception.
**Learning:** Modern Android apps must explicitly opt-out of cleartext traffic in their network security configuration.
**Prevention:** Always define a network_security_config.xml with <base-config cleartextTrafficPermitted="false" /> and reference it in the AndroidManifest.
