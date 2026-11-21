package vegabobo.languageselector.data

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.LocaleList
import kotlinx.coroutines.CancellationException
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
        return collectLocaleSnapshot(packages.map { it.packageName }) { pkg ->
            val locales = try {
                service.getApplicationLocales(pkg)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                null
            }
            when {
                locales == null -> null
                locales.isEmpty -> ""
                else -> locales[0].toLanguageTag()
            }
        }
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

        var failureCount = 0
        for (entry in desired.values) {
            val localeList = LocaleList(Locale.forLanguageTag(entry.languageTag))
            try {
                service.setApplicationLocales(entry.packageName, localeList)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                failureCount++
            }
        }

        for (pkg in LocaleApplyPlan.packagesToClear(currentlyModified, desired.keys)) {
            try {
                service.setApplicationLocales(pkg, LocaleList.getEmptyLocaleList())
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                failureCount++
            }
        }
        check(failureCount == 0) { "Could not apply language settings for $failureCount apps" }
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
