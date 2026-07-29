## 2024-06-28 - Information Exposure through Stack Traces
**Vulnerability:** Application logs expose internal execution paths and details via `e.stackTraceToString()` in production exception blocks.
**Learning:** Using `e.stackTraceToString()` can leak sensitive implementation details, file structures, and library versions to logcat, which might be readable by other apps or malicious users.
**Prevention:** Use `e.message` or `e.message.toString()` to log error contexts safely without exposing the full stack trace.
