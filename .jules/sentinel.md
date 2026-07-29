## 2024-06-29 - Prevent Information Leakage in Logs
**Vulnerability:** Application logs were exposing internal execution paths by calling `e.stackTraceToString()` in production catch blocks.
**Learning:** Hardcoded stack traces in logs could inadvertently expose sensitive application structure, internal logic, or framework details to malicious users examining Logcat.
**Prevention:** Instead of using `.stackTraceToString()`, use `e.message` or `e.message.toString()` (which safely handles nullable strings) in production logs to provide sufficient error context without overexposing execution details.
