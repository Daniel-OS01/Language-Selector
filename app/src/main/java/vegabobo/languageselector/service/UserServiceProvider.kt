package vegabobo.languageselector.service

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vegabobo.languageselector.IUserService
import vegabobo.languageselector.ui.screen.main.OperationMode

object UserServiceProvider {

    private val tag = this.javaClass.simpleName

    var connection = Connection()
    var opMode = OperationMode.NONE

    /**
     * Suspending, non-throwing alternative to the old blocking getService().
     * Returns null when the service is not available within 20 s instead of
     * throwing, so callers can surface a graceful "service unavailable" state.
     */
    suspend fun getServiceOrNull(): IUserService? {
        if (isConnected()) return connection.SERVICE
        var elapsed = 0
        while (!isConnected()) {
            elapsed += 1000
            if (elapsed > 20000) {
                Log.e(tag, "Service unavailable after 20s.")
                return null
            }
            delay(1000)
            Log.d(tag, "Service unavailable, checking again in 1s.. [${elapsed / 1000}s/20s]")
        }
        val svc = connection.SERVICE!!
        val uid = svc.uid
        Log.d(tag, "IUserService available, uid: $uid")
        if (uid == 0) opMode = OperationMode.ROOT
        if (uid <= 2000) opMode = OperationMode.SHIZUKU
        return svc
    }

    fun run(
        onFail: () -> Unit = {},
        onConnected: suspend IUserService.() -> Unit,
    ) {
        fun service() = connection.SERVICE!!
        CoroutineScope(Dispatchers.IO).launch {
            if (isConnected()) {
                onConnected(service())
                return@launch
            }
            var timeout = 0
            while (!isConnected()) {
                timeout += 1000
                if (timeout > 20000) {
                    Log.e(tag, "Service unavailable.")
                    onFail()
                    return@launch
                }
                delay(1000)
                Log.d(tag, "Service unavailable, checking again in 1s.. [${timeout / 1000}s/20s]")
            }
            val serviceUid = service().uid
            Log.d(tag, "IUserService available, uid: $serviceUid")
            if (serviceUid == 0) opMode = OperationMode.ROOT
            if (serviceUid <= 2000) opMode = OperationMode.SHIZUKU
            onConnected(service())
        }
    }

    fun isConnected(): Boolean {
        return connection.SERVICE != null
    }
}
