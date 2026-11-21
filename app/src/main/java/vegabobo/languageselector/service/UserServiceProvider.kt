package vegabobo.languageselector.service

import android.util.Log
import kotlinx.coroutines.CancellationException
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
        val svc = connection.SERVICE ?: return null
        val uid = try {
            svc.uid
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            return null
        }
        Log.d(tag, "IUserService available, uid: $uid")
        if (uid == 0) opMode = OperationMode.ROOT
        if (uid <= 2000) opMode = OperationMode.SHIZUKU
        return svc
    }

    fun run(
        onFail: () -> Unit = {},
        onConnected: suspend IUserService.() -> Unit,
    ) {
        suspend fun invokeConnected() {
            val service = connection.SERVICE
            if (service == null) {
                onFail()
                return
            }
            try {
                onConnected(service)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                Log.w(tag, "Language service operation failed")
                onFail()
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            if (isConnected()) {
                invokeConnected()
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
            val serviceUid = try {
                connection.SERVICE?.uid ?: run {
                    onFail()
                    return@launch
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                onFail()
                return@launch
            }
            Log.d(tag, "IUserService available, uid: $serviceUid")
            if (serviceUid == 0) opMode = OperationMode.ROOT
            if (serviceUid <= 2000) opMode = OperationMode.SHIZUKU
            invokeConnected()
        }
    }

    fun isConnected(): Boolean {
        return connection.SERVICE != null
    }
}
