package vegabobo.languageselector.service

import android.app.ActivityManager
import android.app.IActivityManager
import android.app.IActivityTaskManager
import android.app.ILocaleManager
import android.os.Build
import android.os.LocaleList
import android.os.Process
import android.util.Log
import rikka.shizuku.SystemServiceHelper
import vegabobo.languageselector.BuildConfig
import vegabobo.languageselector.IUserService
import kotlin.system.exitProcess


class UserService : IUserService.Stub() {

    override fun exit() {
        destroy()
    }

    override fun destroy() {
        exitProcess(0)
    }

    override fun getUid(): Int {
        return Process.myUid()
    }

    private fun getCurrentUser(): Int {
        return ActivityManager::class.java
            .getDeclaredMethod("getCurrentUser")
            .invoke(null) as Int
    }

    var LOCALE_MANAGER: ILocaleManager? = null
    fun requiresLocaleManager() {
        if (LOCALE_MANAGER != null) return
        val localeBinder = SystemServiceHelper.getSystemService("locale")
        LOCALE_MANAGER = ILocaleManager.Stub.asInterface(localeBinder)
    }

    override fun setApplicationLocales(packageName: String?, locales: LocaleList?) {
        requiresLocaleManager()
        val currentUser = getCurrentUser()
        // Android 13 (API 33) shipped the 3-arg overload; 13 QPR1+ and 14+ use 4-arg.
        // Catch NoSuchMethodError so a changed hidden-AIDL shape on a future release
        // degrades gracefully rather than hard-crashing.
        try {
            if (Build.VERSION.SDK_INT == 33 && Build.VERSION.RELEASE_OR_CODENAME != "UpsideDownCake") {
                LOCALE_MANAGER!!.setApplicationLocales(packageName, currentUser, locales)
            } else {
                LOCALE_MANAGER!!.setApplicationLocales(packageName, currentUser, locales, true)
            }
        } catch (e: NoSuchMethodError) {
            Log.w(
                BuildConfig.APPLICATION_ID,
                "setApplicationLocales 4-arg failed, falling back to 3-arg: ${e.message}"
            )
            try {
                LOCALE_MANAGER!!.setApplicationLocales(packageName, currentUser, locales)
            } catch (e2: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, "setApplicationLocales fallback also failed: ${e2.message}")
            }
        } catch (e: Exception) {
            Log.e(BuildConfig.APPLICATION_ID, "setApplicationLocales failed: ${e.message}")
        }
    }

    override fun getApplicationLocales(packageName: String?): LocaleList {
        requiresLocaleManager()
        val currentUser = getCurrentUser()
        return try {
            LOCALE_MANAGER!!.getApplicationLocales(packageName, currentUser)
        } catch (e: Exception) {
            Log.w(BuildConfig.APPLICATION_ID, "getApplicationLocales failed for $packageName: ${e.message}")
            LocaleList.getEmptyLocaleList()
        }
    }

    override fun getSystemLocales(): LocaleList {
        requiresLocaleManager()
        return try {
            LOCALE_MANAGER!!.systemLocales
        } catch (e: Exception) {
            Log.w(BuildConfig.APPLICATION_ID, "getSystemLocales failed: ${e.message}")
            LocaleList.getDefault()
        }
    }

    var ACTIVITY_MANAGER: IActivityManager? = null
    fun requiresActivityManager() {
        if (ACTIVITY_MANAGER != null) return
        val am = SystemServiceHelper.getSystemService("activity")
        ACTIVITY_MANAGER = IActivityManager.Stub.asInterface(am)
    }

    override fun forceStopPackage(packageName: String?) {
        requiresActivityManager()
        val currentUser = getCurrentUser()
        ACTIVITY_MANAGER!!.forceStopPackage(packageName, currentUser)
    }

    var ACTIVITY_TASK_MANAGER: IActivityTaskManager? = null
    fun requiresActivityTaskManager() {
        if (ACTIVITY_TASK_MANAGER != null) return
        val am = SystemServiceHelper.getSystemService("activity_task")
        ACTIVITY_TASK_MANAGER = IActivityTaskManager.Stub.asInterface(am)
    }

    override fun getFirstRunningTaskPackage(): String {
        requiresActivityTaskManager()
        val runningTask =
            try {
                ACTIVITY_TASK_MANAGER!!.getTasks(1, false, false, -1).first()
            } catch (e: NoSuchMethodError) {
                Log.w(
                    BuildConfig.APPLICATION_ID,
                    "getTasks failed, trying again without displayId, error: ${e.stackTraceToString()}"
                )
                ACTIVITY_TASK_MANAGER!!.getTasks(1, false, false).first()
            }
        return runningTask.topActivity?.packageName ?: ""
    }
}
