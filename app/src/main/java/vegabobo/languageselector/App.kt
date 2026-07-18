package vegabobo.languageselector

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.lsposed.hiddenapibypass.HiddenApiBypass

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Exempt all hidden APIs under the android.* / java.* etc. trees before any
        // reflection or hidden-AIDL use can happen elsewhere in the process.
        HiddenApiBypass.addHiddenApiExemptions("L")
    }
}