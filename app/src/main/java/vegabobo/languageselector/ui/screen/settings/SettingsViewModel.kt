package vegabobo.languageselector.ui.screen.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.redundent.kotlin.xml.xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import vegabobo.languageselector.dao.AppInfoDao
import vegabobo.languageselector.dao.AppInfoEntity
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

enum class ExportState {
    NONE,
    SUCCESS,
    ERROR
}

data class SettingsState(
    val exportState: ExportState = ExportState.NONE,
    val errorText: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appInfoDao: AppInfoDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState = _uiState.asStateFlow()

    fun getFileName(extension: String): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        return "languages-select-app-config-$currentDate.$extension"
    }

    fun exportAsJson(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val apps = appInfoDao.getAll()
                val json = Gson().toJson(apps)
                withContext(Dispatchers.IO) {
                    context.contentResolver.openFileDescriptor(uri, "w")?.use {
                        FileOutputStream(it.fileDescriptor).use { fos ->
                            fos.write(json.toByteArray())
                        }
                    }
                }
                _uiState.value = SettingsState(exportState = ExportState.SUCCESS)
            } catch (e: Exception) {
                _uiState.value = SettingsState(
                    exportState = ExportState.ERROR,
                    errorText = e.stackTraceToString()
                )
            }
        }
    }

    fun importFromJson(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val json = reader.readText()
                val listType = object : TypeToken<List<AppInfoEntity>>() {}.type
                val apps = Gson().fromJson<List<AppInfoEntity>>(json, listType)
                appInfoDao.importApps(apps)
                _uiState.value = SettingsState(exportState = ExportState.SUCCESS)
            } catch (e: Exception) {
                _uiState.value = SettingsState(
                    exportState = ExportState.ERROR,
                    errorText = e.stackTraceToString()
                )
            }
        }
    }

    fun exportAsXml(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val apps = appInfoDao.getAll()
                val xmlString = xml("apps") {
                    for (app in apps) {
                        element("app") {
                            element("pkg", app.pkg)
                            element("name", app.name)
                            element("last_selected", app.lastSelected ?: "")
                        }
                    }
                }.toString()

                withContext(Dispatchers.IO) {
                    context.contentResolver.openFileDescriptor(uri, "w")?.use {
                        FileOutputStream(it.fileDescriptor).use { fos ->
                            fos.write(xmlString.toByteArray())
                        }
                    }
                }
                _uiState.value = SettingsState(exportState = ExportState.SUCCESS)
            } catch (e: Exception) {
                _uiState.value = SettingsState(
                    exportState = ExportState.ERROR,
                    errorText = e.stackTraceToString()
                )
            }
        }
    }

    fun importFromXml(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val xmlString = reader.readText()

                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val parser = factory.newPullParser()
                parser.setInput(StringReader(xmlString))

                val apps = mutableListOf<AppInfoEntity>()
                var eventType = parser.eventType
                var currentApp: AppInfoEntity? = null
                var currentTag: String? = null

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            currentTag = parser.name
                            if (currentTag == "app") {
                                currentApp = AppInfoEntity("", "", null)
                            }
                        }
                        XmlPullParser.TEXT -> {
                            val text = parser.text
                            if (text != null && text.isNotBlank()) {
                                when (currentTag) {
                                    "pkg" -> currentApp = currentApp?.copy(pkg = text)
                                    "name" -> currentApp = currentApp?.copy(name = text)
                                    "last_selected" -> {
                                        val lastSelected = text.toLongOrNull()
                                        currentApp = currentApp?.copy(lastSelected = lastSelected)
                                    }
                                }
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (parser.name == "app") {
                                currentApp?.let { apps.add(it) }
                            }
                            currentTag = null
                        }
                    }
                    eventType = parser.next()
                }

                appInfoDao.importApps(apps)
                _uiState.value = SettingsState(exportState = ExportState.SUCCESS)
            } catch (e: Exception) {
                _uiState.value = SettingsState(
                    exportState = ExportState.ERROR,
                    errorText = e.stackTraceToString()
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = SettingsState()
    }
}