package vegabobo.languageselector.ui.screen.settings

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vegabobo.languageselector.data.AppListRefreshBus
import vegabobo.languageselector.data.LocaleBackup
import vegabobo.languageselector.data.LocaleBackupCodec
import vegabobo.languageselector.data.LocaleBackupLimits
import vegabobo.languageselector.data.LocalePreset
import vegabobo.languageselector.data.LocaleSnapshot
import vegabobo.languageselector.service.UserServiceProvider
import javax.inject.Inject

data class SettingsUiState(
    val presets: List<LocalePreset> = emptyList(),
    val isBusy: Boolean = false,
    val message: String? = null,
    val showSaveDialog: Boolean = false,
    val saveNameDraft: String = "",
)

@HiltViewModel
class SettingsVm @Inject constructor(
    private val app: Application,
    private val sp: SharedPreferences,
    private val localeSnapshot: LocaleSnapshot,
    private val refreshBus: AppListRefreshBus,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        reloadPresets()
    }

    fun reloadPresets() {
        _uiState.update { it.copy(presets = LocaleBackupCodec.loadPresets(sp)) }
    }

    fun openSaveDialog() {
        _uiState.update { it.copy(showSaveDialog = true, saveNameDraft = "") }
    }

    fun dismissSaveDialog() {
        _uiState.update { it.copy(showSaveDialog = false, saveNameDraft = "") }
    }

    fun onSaveNameChange(value: String) {
        _uiState.update { it.copy(saveNameDraft = value) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun saveCurrentAsPreset() {
        val name = _uiState.value.saveNameDraft
        runBusy {
            requireServiceConnected()
            val current = LocaleBackupCodec.loadPresets(sp)
            require(current.size < LocaleBackupLimits.MAX_PRESETS) {
                "Preset limit reached (${LocaleBackupLimits.MAX_PRESETS})"
            }
            val apps = localeSnapshot.collectModifiedApps()
            val preset = LocaleBackupCodec.createPreset(name, apps)
            LocaleBackupCodec.savePresets(sp, current + preset)
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        presets = LocaleBackupCodec.loadPresets(sp),
                        showSaveDialog = false,
                        saveNameDraft = "",
                        message = "Preset saved (${apps.size} apps)",
                    )
                }
            }
        }
    }

    fun applyPreset(presetId: String) {
        runBusy {
            requireServiceConnected()
            val preset = LocaleBackupCodec.loadPresets(sp).firstOrNull { it.id == presetId }
                ?: error("Preset not found")
            localeSnapshot.applySnapshot(preset.apps)
            refreshBus.requestRefresh()
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(message = "Applied \"${preset.name}\"")
                }
            }
        }
    }

    fun deletePreset(presetId: String) {
        val next = LocaleBackupCodec.loadPresets(sp).filterNot { it.id == presetId }
        LocaleBackupCodec.savePresets(sp, next)
        _uiState.update {
            it.copy(presets = next, message = "Preset deleted")
        }
    }

    fun exportBackup(uri: Uri) {
        runBusy {
            requireServiceConnected()
            val backup = LocaleBackup(
                schemaVersion = LocaleBackupCodec.SCHEMA_VERSION,
                exportedAt = System.currentTimeMillis(),
                apps = localeSnapshot.collectModifiedApps(),
                pinnedLocales = LocaleBackupCodec.loadPinnedLocales(sp),
                presets = LocaleBackupCodec.loadPresets(sp),
            )
            val json = LocaleBackupCodec.encodeBackup(backup)
            app.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(json.toByteArray(Charsets.UTF_8))
            } ?: error("Could not write export file")
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(message = "Exported ${backup.apps.size} apps")
                }
            }
        }
    }

    fun importBackup(uri: Uri) {
        runBusy {
            requireServiceConnected()
            val json = app.contentResolver.openInputStream(uri)?.use { stream ->
                stream.readBytes().toString(Charsets.UTF_8)
            } ?: error("Could not read import file")
            val backup = LocaleBackupCodec.decodeBackup(json)
            localeSnapshot.applySnapshot(backup.apps)
            LocaleBackupCodec.savePinnedLocales(sp, backup.pinnedLocales)
            LocaleBackupCodec.savePresets(sp, backup.presets.take(LocaleBackupLimits.MAX_PRESETS))
            refreshBus.requestRefresh()
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        presets = LocaleBackupCodec.loadPresets(sp),
                        message = "Imported ${backup.apps.size} apps",
                    )
                }
            }
        }
    }

    private fun requireServiceConnected() {
        if (!UserServiceProvider.isConnected()) {
            error("Language service unavailable. Start Shizuku or grant root first.")
        }
    }

    private fun runBusy(block: suspend () -> Unit) {
        if (_uiState.value.isBusy) return
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isBusy = true, message = null) }
            try {
                block()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(message = e.message ?: "Operation failed")
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isBusy = false) }
                }
            }
        }
    }
}
