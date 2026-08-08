## 2025-02-18 - Disable Cleartext Traffic in Android Network Security Config
**Vulnerability:** Application allows unencrypted HTTP traffic by default, risking MitM attacks.
**Learning:** Default Android configurations may allow cleartext traffic; explicit disallowal in `network_security_config.xml` is necessary for strict network security.
**Prevention:** Always include a `network_security_config.xml` with `<base-config cleartextTrafficPermitted="false" />` when starting a new Android project to enforce HTTPS.
