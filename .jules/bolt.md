## 2024-06-23 - Avoid redundant ICU lookups for Locales
**Learning:** In Android, `locale.getDisplayLanguage(locale)` can be a performance bottleneck when called in a loop for 1000+ locales because it triggers expensive ICU (International Components for Unicode) lookups.
**Action:** Cache these translations using the base language code (`locale.language`) as the key to significantly reduce execution time and prevent app load lag.
