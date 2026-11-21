package vegabobo.languageselector.ui.screen.main

import android.app.Application
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.coroutineContext
import org.json.JSONArray
import org.json.JSONObject
import rikka.shizuku.Shizuku
import vegabobo.languageselector.BuildConfig
import vegabobo.languageselector.IUserService
import vegabobo.languageselector.RootReceivedListener
import vegabobo.languageselector.dao.AppInfoDb
import vegabobo.languageselector.data.AppListRefreshBus
import vegabobo.languageselector.service.UserServiceProvider
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject


@HiltViewModel
class MainScreenVm @Inject constructor(
    val app: Application,
    appInfoDb: AppInfoDb,
    private val sharedPreferences: SharedPreferences,
    private val refreshBus: AppListRefreshBus,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MainScreenState(
            isShowSystemAppsHome = sharedPreferences.getBoolean(PREF_SHOW_SYSTEM_APPS_HOME, false)
        )
    )
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()
    var lastSelectedApp: AppInfo? = null
    val dao = appInfoDb.appInfoDao()
    private var fillListJob: Job? = null
    private var reloadJob: Job? = null
    private var searchJob: Job? = null
    private val loadMutex = Mutex()
    private var invalidateCacheOnNextLoad = false
    private val shizukuPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { _, grantResult ->
            viewModelScope.launch {
                if (_uiState.value.operationMode != OperationMode.ROOT) {
                    _uiState.update {
                        it.copy(operationMode = if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            OperationMode.SHIZUKU
                        } else {
                            OperationMode.NONE
                        })
                    }
                    refreshFromSystem()
                }
            }
        }

    // Preserve known locale labels between ordinary loads; explicit refresh clears
    // this and the persisted cache before rebuilding from the system.
    private val appInfoCache = ConcurrentHashMap<String, AppInfo>()

    fun getIndexFromAppInfoItem(): Int {
        return _uiState.value.listOfApps.indexOfFirst { it.pkg == lastSelectedApp?.pkg }
    }

    private suspend fun loadOperationMode() {
        if (Shell.getShell().isAlive) Shell.getShell().close()
        Shell.getShell()
        val mode = when {
            Shell.isAppGrantedRoot() == true -> OperationMode.ROOT
            Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED ->
                OperationMode.SHIZUKU
            else -> OperationMode.NONE
        }
        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(operationMode = mode) }
            if (mode == OperationMode.ROOT) RootReceivedListener.onRootReceived()
        }
    }

    init {
        Shizuku.addRequestPermissionResultListener(shizukuPermissionResultListener)
        fillListOfApps()
        viewModelScope.launch {
            refreshBus.requests.collect {
                refreshFromSystem()
            }
        }
    }

    override fun onCleared() {
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionResultListener)
        super.onCleared()
    }

    fun parseBasicAppInfo(a: ApplicationInfo): AppInfo {
        val isSystemApp = (a.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val labels = arrayListOf<AppLabels>()
        if (isSystemApp)
            labels.add(AppLabels.SYSTEM_APP)
        return AppInfo(
            name = app.packageManager.getLabel(a),
            pkg = a.packageName,
            labels = labels,
            iconVersion = runCatching { File(a.sourceDir).lastModified() }.getOrDefault(0L),
        )
    }

    private fun sortApps(apps: List<AppInfo>): List<AppInfo> =
        apps.sortedWith(compareByDescending<AppInfo> { it.isModified() }.thenBy { it.name.lowercase() })

    fun refreshFromSystem() {
        invalidateCacheOnNextLoad = true
        _uiState.update { it.copy(isLoading = true) }
        fillListOfApps()
    }

    fun fillListOfApps() {
        // All callers and job-handle assignment run on Main. The mutex also waits for
        // non-suspending PackageManager/Binder work in an old cancelled load to finish
        // before the replacement invalidates either cache.
        fillListJob?.cancel()
        reloadJob?.cancel()
        searchJob?.cancel()
        fillListJob = viewModelScope.launch {
            loadMutex.withLock {
                val invalidate = invalidateCacheOnNextLoad
                if (invalidate) {
                    withContext(Dispatchers.IO) {
                        appInfoCache.clear()
                        sharedPreferences.edit().remove(PREF_APP_LIST_CACHE).apply()
                    }
                    // Consume only after successful dispatch back to Main. A cancelled
                    // replacement must not lose a pending invalidation request.
                    invalidateCacheOnNextLoad = false
                }
                withContext(Dispatchers.IO) {
                    if (_uiState.value.operationMode == OperationMode.CHECKING) {
                        loadOperationMode()
                    }
                    val hasAccess = _uiState.value.operationMode != OperationMode.NONE
                    val cachedApps = loadCachedAppList().map { appInfo ->
                        appInfo.withModifiedState(null, hasAccess)
                    }
                    if (invalidate || !hasAccess) {
                        withContext(Dispatchers.Main) {
                            val cleared = _uiState.value.listOfApps.map {
                                it.withModifiedState(false, hasAccess)
                            }
                            replaceAppLists(sortApps(cleared))
                            _uiState.value.history.clear()
                        }
                    }
                    val showedCachedApps = displayCachedAppList(cachedApps)
                    val cachedByPackage = cachedApps.associateBy { it.pkg }
                    val packages = getInstalledPackages()
                    val basicList = packages.map { packageInfo ->
                        coroutineContext.ensureActive()
                        val basic = parseBasicAppInfo(packageInfo)
                        val cached = appInfoCache[basic.pkg] ?: cachedByPackage[basic.pkg]
                        basic.withModifiedState(cached?.isModified(), hasAccess)
                    }
                    val sorted = sortApps(basicList)
                    replaceAppLists(sorted)
                    coroutineContext.ensureActive()
                    saveCachedAppList(sorted)
                    if (!showedCachedApps) {
                        withContext(Dispatchers.Main) {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                    if (hasAccess) scanLanguageStates(packages)
                }
            }
        }
    }

    private suspend fun scanLanguageStates(packages: List<ApplicationInfo>) {
        // This is part of fillListJob, never a new viewModelScope job. Refresh and
        // permission changes cancel the *whole* pipeline, including this scan.
        val service = UserServiceProvider.getServiceOrNull() ?: return
        val observations = mutableMapOf<String, Boolean>()
        var lastUiUpdateAt = 0L
        for ((index, packageInfo) in packages.withIndex()) {
            coroutineContext.ensureActive()
            val modified = readModifiedState(service, packageInfo.packageName)
            coroutineContext.ensureActive()
            // A failed Binder read is unknown, not a successful empty locale list.
            if (modified != null) observations[packageInfo.packageName] = modified
            val now = System.currentTimeMillis()
            if ((index + 1) % LOCALE_SCAN_BATCH_SIZE == 0 &&
                now - lastUiUpdateAt >= LOCALE_SCAN_UPDATE_MIN_INTERVAL_MS
            ) {
                applyLanguageScan(observations)
                lastUiUpdateAt = now
            }
            if ((index + 1) % LOCALE_SCAN_COOPERATIVE_YIELD_SIZE == 0) delay(1)
        }
        applyLanguageScan(observations)
    }

    private fun readModifiedState(service: IUserService, pkg: String): Boolean? = try {
        !service.getApplicationLocales(pkg).isEmpty
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }

    private suspend fun applyLanguageScan(observations: Map<String, Boolean>) {
        val sortedApps = withContext(Dispatchers.Main) {
            sortApps(_uiState.value.listOfApps.map { appInfo ->
                appInfo.withModifiedState(observations[appInfo.pkg], true)
            }).also { apps ->
                appInfoCache.clear()
                appInfoCache.putAll(apps.associateBy { it.pkg })
                replaceAppLists(apps)
            }
        }
        coroutineContext.ensureActive()
        saveCachedAppList(sortedApps)
    }

    private fun AppInfo.withModifiedState(observed: Boolean?, hasAccess: Boolean): AppInfo {
        val modified = resolveModifiedState(isModified(), observed, hasAccess)
        val nextLabels = labels.filterNot { it == AppLabels.MODIFIED }
        return copy(labels = if (modified) nextLabels + AppLabels.MODIFIED else nextLabels)
    }

    private suspend fun displayCachedAppList(cachedApps: List<AppInfo>): Boolean {
        if (cachedApps.isEmpty()) {
            return false
        }
        replaceAppLists(cachedApps)
        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(isLoading = false) }
        }
        return true
    }

    private fun loadCachedAppList(): List<AppInfo> {
        val raw = sharedPreferences.getString(PREF_APP_LIST_CACHE, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            val apps = ArrayList<AppInfo>(array.length())
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val pkg = item.optString("pkg")
                if (pkg.isBlank() || pkg == BuildConfig.APPLICATION_ID) continue

                val labels = arrayListOf<AppLabels>()
                if (item.optBoolean("system", false)) labels.add(AppLabels.SYSTEM_APP)
                if (item.optBoolean("modified", false)) labels.add(AppLabels.MODIFIED)
                apps.add(
                    AppInfo(
                        name = item.optString("name", pkg),
                        pkg = pkg,
                        labels = labels,
                        iconVersion = item.optLong("iconVersion", 0L),
                    )
                )
            }
            apps.distinctBy { it.pkg }
        }.getOrDefault(emptyList())
    }

    private fun saveCachedAppList(apps: List<AppInfo>) {
        val array = JSONArray()
        for (appInfo in apps) {
            array.put(
                JSONObject()
                    .put("pkg", appInfo.pkg)
                    .put("name", appInfo.name)
                    .put("system", appInfo.isSystemApp())
                    .put("modified", appInfo.isModified())
                    .put("iconVersion", appInfo.iconVersion)
            )
        }
        sharedPreferences.edit()
            .putString(PREF_APP_LIST_CACHE, array.toString())
            .apply()
    }

    private suspend fun replaceAppLists(apps: List<AppInfo>) {
        withContext(Dispatchers.Main) {
            _uiState.value.listOfApps.clear()
            _uiState.value.listOfApps.addAll(apps)
            launchSearch(_uiState.value.searchTextFieldValue, debounce = false)
        }
    }

    fun getInstalledPackages(): List<ApplicationInfo> {
        return app.packageManager.getInstalledApplications(
            PackageManager.ApplicationInfoFlags.of(0)
        ).mapNotNull {
            if (!it.enabled || BuildConfig.APPLICATION_ID == it.packageName)
                null
            else
                it
        }
    }

    fun toggleDropdown() {
        val newDropdownVisibility = !uiState.value.isDropdownVisible
        _uiState.update { it.copy(isDropdownVisible = newDropdownVisibility) }
    }

    fun toggleSystemAppsVisibility() {
        val newShowSystemApps = !uiState.value.isShowSystemAppsHome
        sharedPreferences.edit()
            .putBoolean(PREF_SHOW_SYSTEM_APPS_HOME, newShowSystemApps)
            .apply()
        _uiState.update { it.copy(isShowSystemAppsHome = newShowSystemApps) }
        toggleDropdown()
    }

    fun onClickProceedShizuku() {
        _uiState.update { it.copy(operationMode = OperationMode.CHECKING) }
        refreshFromSystem()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
        private const val LOCALE_SCAN_BATCH_SIZE = 80
        private const val LOCALE_SCAN_UPDATE_MIN_INTERVAL_MS = 500L
        private const val LOCALE_SCAN_COOPERATIVE_YIELD_SIZE = 40
        private const val PREF_SHOW_SYSTEM_APPS_HOME = "show_system_apps_home"
        private const val PREF_APP_LIST_CACHE = "app_list_cache"
    }

    fun onSearchTextFieldChange(newText: String) {
        val normalized = newText.replace(Regex("[\r\n]"), "")
        val triggeredByImeSearch = newText.any { it == '\n' || it == '\r' }
        val previousValue = _uiState.value.searchTextFieldValue
        if (!triggeredByImeSearch && previousValue == normalized) {
            return
        }

        _uiState.update { it.copy(searchTextFieldValue = normalized) }
        launchSearch(normalized, debounce = !triggeredByImeSearch)
    }

    fun onSearchConfirmed(query: String) {
        val normalized = query.replace(Regex("[\r\n]"), "")
        _uiState.update { it.copy(searchTextFieldValue = normalized) }
        launchSearch(normalized, debounce = false)
    }

    private fun launchSearch(query: String, debounce: Boolean) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (debounce) {
                delay(SEARCH_DEBOUNCE_MS)
            }

            val appsSnapshot = _uiState.value.listOfApps.toList()
            val selectedLabels = _uiState.value.selectLabels.toList()
            val requireModified = selectedLabels.contains(AppLabels.MODIFIED)
            val showSystemApps = selectedLabels.contains(AppLabels.SYSTEM_APP)
            val normalizedQuery = query.trim().lowercase()

            val results = withContext(Dispatchers.Default) {
                val queryFiltered = if (normalizedQuery.isEmpty()) {
                    appsSnapshot
                } else {
                    appsSnapshot.filter {
                        ensureActive()
                        it.pkg.lowercase().contains(normalizedQuery) ||
                                it.name.lowercase().contains(normalizedQuery)
                    }
                }

                queryFiltered.filter { app ->
                    ensureActive()
                    if (requireModified && !app.isModified()) {
                        return@filter false
                    }

                    if (!showSystemApps && app.isSystemApp()) {
                        return@filter false
                    }

                    true
                }
            }

            val searchResults = _uiState.value.searchResults
            searchResults.clear()
            searchResults.addAll(results)
        }
    }

    fun onSearchExpandedChange() {
        val isExpanded = !uiState.value.isExpanded
        _uiState.update { it.copy(isExpanded = isExpanded) }
        if (isExpanded)
            updateHistory()
        else {
            _uiState.update { it.copy(searchTextFieldValue = "") }
            launchSearch("", debounce = false)
        }
    }

    fun onSelectedLabelChange(label: AppLabels) {
        val lb = _uiState.value.selectLabels
        if (lb.contains(label))
            lb.remove(label)
        else
            lb.add(label)
        launchSearch(_uiState.value.searchTextFieldValue, debounce = false)
    }

    fun updateHistory() {
        viewModelScope.launch {
            val packages = withContext(Dispatchers.IO) { dao.getHistory().map { it.pkg } }
            val apps = _uiState.value.listOfApps.associateBy { it.pkg }
            val history = packages.mapNotNull { apps[it] }
            _uiState.value.history.clear()
            _uiState.value.history.addAll(history)
        }
    }

    fun addAppToHistory(ai: AppInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            if (dao.findByPkg(ai.pkg) == null) {
                dao.insert(ai.toAppInfoEntity())
            }
            dao.setLastSelected(ai.pkg, System.currentTimeMillis())
            updateHistory()
        }
    }

    fun onClickClear() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.cleanLastSelectedAll()
            updateHistory()
        }
    }

    fun reloadLastSelectedItem() {
        val selected = lastSelectedApp ?: return
        reloadJob?.cancel()
        reloadJob = viewModelScope.launch {
            loadMutex.withLock {
                if (_uiState.value.operationMode == OperationMode.NONE) return@withLock
                val updatedAi = withContext(Dispatchers.IO) {
                    val service = UserServiceProvider.getServiceOrNull() ?: return@withContext null
                    val modified = readModifiedState(service, selected.pkg) ?: return@withContext null
                    val pkg = try {
                        app.packageManager.getApplicationInfo(selected.pkg, 0)
                    } catch (_: PackageManager.NameNotFoundException) {
                        return@withContext null
                    }
                    parseBasicAppInfo(pkg).withModifiedState(modified, true)
                } ?: return@withLock
                val apps = _uiState.value.listOfApps
                val idx = apps.indexOfFirst { it.pkg == updatedAi.pkg }
                if (idx != -1 && updatedAi.labels != apps[idx].labels) {
                    appInfoCache[updatedAi.pkg] = updatedAi
                    apps[idx] = updatedAi
                    val newList = sortApps(apps)
                    replaceAppLists(newList)
                    _uiState.update {
                        it.copy(snackBarDisplay = if (updatedAi.isModified()) {
                            SnackBarDisplay.MOVED_TO_TOP
                        } else {
                            SnackBarDisplay.MOVED_TO_BOTTOM
                        })
                    }
                    withContext(Dispatchers.IO) { saveCachedAppList(newList) }
                }
            }
        }
    }

    fun resetSnackBarDisplay() = _uiState.update { it.copy(snackBarDisplay = SnackBarDisplay.NONE) }

    fun onClickApp(ai: AppInfo) {
        lastSelectedApp = ai
        addAppToHistory(ai)
    }
}
