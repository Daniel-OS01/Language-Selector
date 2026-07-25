## 2024-07-25 - Disable Cleartext HTTP Traffic
**Vulnerability:** Android application allows cleartext (HTTP) traffic, which can expose sensitive data to interception.
**Learning:** Explicitly declaring a network security configuration ensures that strict HTTPS usage is enforced across the application to prevent data leakage over unencrypted channels.
**Prevention:** Always include a network security configuration file with `<base-config cleartextTrafficPermitted="false" />` in new Android projects and reference it in the AndroidManifest.xml.
