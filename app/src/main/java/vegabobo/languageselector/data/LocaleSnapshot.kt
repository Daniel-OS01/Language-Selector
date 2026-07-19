package vegabobo.languageselector.data

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.LocaleList
import vegabobo.languageselector.BuildConfig
import vegabobo.languageselector.IUserService
import vegabobo.languageselector.service.UserServiceProvider
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleSnapshot @Inject constructor(
    private val app: Application,
) {
    suspend fun collectModifiedApps(): List<AppLocaleEntry> {
        val service = UserServiceProvider.getServiceOrNull()
            ?: error("Language service unavailable. Start Shizuku or grant root first.")
        return collectModifiedApps(service, getInstalledPackages())
    }

    fun collectModifiedApps(
        service: IUserService,
        packages: List<ApplicationInfo>,
    ): List<AppLocaleEntry> {
        val out = ArrayList<AppLocaleEntry>()
        for (pkg in packages) {
            val locales = try {
                service.getApplicationLocales(pkg.packageName)
            } catch (_: Exception) {
                null
            } ?: continue
            if (locales.isEmpty) continue
            val tag = locales[0].toLanguageTag()
            if (tag.isNotBlank()) {
                out.add(AppLocaleEntry(pkg.packageName, tag))
            }
        }
        return out.sortedBy { it.packageName }
    }

    suspend fun applySnapshot(apps: List<AppLocaleEntry>) {
        val service = UserServiceProvider.getServiceOrNull()
            ?: error("Language service unavailable. Start Shizuku or grant root first.")
        applySnapshot(service, getInstalledPackages(), apps)
    }

    fun applySnapshot(
        service: IUserService,
        packages: List<ApplicationInfo>,
        apps: List<AppLocaleEntry>,
    ) {
        val installed = packages.map { it.packageName }.toHashSet()
        val desired = apps
            .filter { it.packageName in installed && it.languageTag.isNotBlank() }
            .associateBy { it.packageName }

        val currentlyModified = collectModifiedApps(service, packages)
            .map { it.packageName }

        for (entry in desired.values) {
            val localeList = LocaleList(Locale.forLanguageTag(entry.languageTag))
            try {
                service.setApplicationLocales(entry.packageName, localeList)
            } catch (_: Exception) {
                // Continue applying remaining packages.
            }
        }

        for (pkg in LocaleApplyPlan.packagesToClear(currentlyModified, desired.keys)) {
            try {
                service.setApplicationLocales(pkg, LocaleList.getEmptyLocaleList())
            } catch (_: Exception) {
                // Continue clearing remaining packages.
            }
        }
    }

    fun getInstalledPackages(): List<ApplicationInfo> {
        return app.packageManager.getInstalledApplications(
            PackageManager.ApplicationInfoFlags.of(0)
        ).mapNotNull {
            if (!it.enabled || BuildConfig.APPLICATION_ID == it.packageName) {
                null
            } else {
                it
            }
        }
    }
}
