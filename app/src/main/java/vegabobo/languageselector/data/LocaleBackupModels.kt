package vegabobo.languageselector.data

data class AppLocaleEntry(
    val packageName: String,
    val languageTag: String,
)

data class LocalePreset(
    val id: String,
    val name: String,
    val createdAt: Long,
    val apps: List<AppLocaleEntry>,
)

data class LocaleBackup(
    val schemaVersion: Int = 1,
    val exportedAt: Long,
    val apps: List<AppLocaleEntry>,
    val pinnedLocales: List<String>,
    val presets: List<LocalePreset>,
)

object PrefConstants {
    const val PINNED_LOCALES = "pinned_locales"
    const val LOCALE_PRESETS = "locale_presets"
}

object LocaleBackupLimits {
    const val MAX_PRESETS = 10
}

/**
 * Pure helpers for deciding which packages to set vs clear when applying a snapshot.
 */
object LocaleApplyPlan {
    fun packagesToClear(
        currentModified: Collection<String>,
        snapshotPackages: Collection<String>,
    ): List<String> {
        val keep = snapshotPackages.toHashSet()
        return currentModified.filterNot { it in keep }
    }
}
