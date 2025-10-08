package vegabobo.languageselector.ui.screen.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vegabobo.languageselector.dao.AppInfoDao
import vegabobo.languageselector.dao.AppInfoEntity
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.inject.Inject

enum class ErrorType {
    NONE,
    IMPORT,
    EXPORT
}

data class SettingsState(
    val isExportSuccess: Boolean = false,
    val isImportSuccess: Boolean = false,
    val errorType: ErrorType = ErrorType.NONE
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appInfoDao: AppInfoDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState = _uiState.asStateFlow()

    fun exportSettings(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val apps = appInfoDao.getAll()
                val json = Gson().toJson(apps)
                context.contentResolver.openFileDescriptor(uri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use { fos ->
                        fos.write(json.toByteArray())
                    }
                }
                _uiState.value = _uiState.value.copy(isExportSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorType = ErrorType.EXPORT)
            }
        }
    }

    fun importSettings(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val json = reader.readText()
                val listType = object : TypeToken<List<AppInfoEntity>>() {}.type
                val apps = Gson().fromJson<List<AppInfoEntity>>(json, listType)
                appInfoDao.deleteAll()
                appInfoDao.insertAll(apps)
                _uiState.value = _uiState.value.copy(isImportSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorType = ErrorType.IMPORT)
            }
        }
    }

    fun resetState() {
        _uiState.value = SettingsState()
    }
}