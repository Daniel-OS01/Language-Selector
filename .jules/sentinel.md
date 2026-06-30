## 2024-06-30 - Prevent Information Leakage via Stack Traces
**Vulnerability:** Use of e.stackTraceToString() in production catch blocks exposes sensitive internal execution paths.
**Learning:** Hardcoded stack trace printing in production logs can be used by attackers to map out the application's internal structure and discover vulnerabilities.
**Prevention:** Instead of logging the full stack trace, use e.message.toString() to provide error context without exposing sensitive details.
