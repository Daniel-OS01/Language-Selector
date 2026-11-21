package vegabobo.languageselector

import android.app.Application
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.HiltAndroidApp
import org.lsposed.hiddenapibypass.HiddenApiBypass

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(Shell.Builder.create().setTimeout(10))
        // Exempt all hidden APIs under the android.* / java.* etc. trees before any
        // reflection or hidden-AIDL use can happen elsewhere in the process.
        HiddenApiBypass.addHiddenApiExemptions("L")
    }
}