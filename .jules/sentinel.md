## 2026-07-01 - Prevent Information Leakage in Logs
**Vulnerability:** The application was logging complete stack traces in production using e.stackTraceToString().
**Learning:** Exposing detailed stack traces provides potential attackers with deep insights into internal execution paths, which can be exploited.
**Prevention:** Always log safe error messages using e.message.toString() in production code instead of exposing full stack traces to prevent sensitive architectural information from leaking.
