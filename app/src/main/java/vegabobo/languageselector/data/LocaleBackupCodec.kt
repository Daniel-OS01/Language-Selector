package vegabobo.languageselector.data

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object LocaleBackupCodec {
    const val SCHEMA_VERSION = 1

    fun encodeBackup(backup: LocaleBackup): String {
        val root = JSONObject()
        root.put("schemaVersion", backup.schemaVersion)
        root.put("exportedAt", backup.exportedAt)
        root.put("apps", encodeApps(backup.apps))
        root.put("pinnedLocales", JSONArray(backup.pinnedLocales))
        root.put("presets", encodePresets(backup.presets))
        return root.toString()
    }

    fun decodeBackup(json: String): LocaleBackup {
        val root = JSONObject(json)
        val schema = root.optInt("schemaVersion", -1)
        require(schema == SCHEMA_VERSION) {
            "Unsupported backup schemaVersion: $schema"
        }
        return LocaleBackup(
            schemaVersion = schema,
            exportedAt = root.optLong("exportedAt", 0L),
            apps = decodeApps(root.optJSONArray("apps")),
            pinnedLocales = decodeStringList(root.optJSONArray("pinnedLocales")),
            presets = decodePresets(root.optJSONArray("presets")),
        )
    }

    fun encodePresets(presets: List<LocalePreset>): JSONArray {
        val array = JSONArray()
        for (preset in presets) {
            array.put(encodePreset(preset))
        }
        return array
    }

    fun decodePresets(array: JSONArray?): List<LocalePreset> {
        if (array == null) return emptyList()
        val out = ArrayList<LocalePreset>(array.length())
        for (i in 0 until array.length()) {
            out.add(decodePreset(array.getJSONObject(i)))
        }
        return out
    }

    fun loadPresets(sp: SharedPreferences): List<LocalePreset> {
        val raw = sp.getString(PrefConstants.LOCALE_PRESETS, null) ?: return emptyList()
        return decodePresets(JSONArray(raw))
    }

    fun savePresets(sp: SharedPreferences, presets: List<LocalePreset>) {
        sp.edit()
            .putString(PrefConstants.LOCALE_PRESETS, encodePresets(presets).toString())
            .apply()
    }

    fun loadPinnedLocales(sp: SharedPreferences): List<String> {
        return (sp.getStringSet(PrefConstants.PINNED_LOCALES, emptySet()) ?: emptySet())
            .toList()
            .sorted()
    }

    fun savePinnedLocales(sp: SharedPreferences, pinned: Collection<String>) {
        sp.edit()
            .putStringSet(PrefConstants.PINNED_LOCALES, pinned.toSet())
            .apply()
    }

    fun createPreset(
        name: String,
        apps: List<AppLocaleEntry>,
        createdAt: Long = System.currentTimeMillis(),
        id: String = UUID.randomUUID().toString(),
    ): LocalePreset {
        val trimmed = name.trim()
        require(trimmed.isNotEmpty()) { "Preset name must not be blank" }
        return LocalePreset(
            id = id,
            name = trimmed,
            createdAt = createdAt,
            apps = apps,
        )
    }

    private fun encodePreset(preset: LocalePreset): JSONObject {
        return JSONObject()
            .put("id", preset.id)
            .put("name", preset.name)
            .put("createdAt", preset.createdAt)
            .put("apps", encodeApps(preset.apps))
    }

    private fun decodePreset(obj: JSONObject): LocalePreset {
        return LocalePreset(
            id = obj.getString("id"),
            name = obj.getString("name"),
            createdAt = obj.optLong("createdAt", 0L),
            apps = decodeApps(obj.optJSONArray("apps")),
        )
    }

    private fun encodeApps(apps: List<AppLocaleEntry>): JSONArray {
        val array = JSONArray()
        for (app in apps) {
            array.put(
                JSONObject()
                    .put("packageName", app.packageName)
                    .put("languageTag", app.languageTag)
            )
        }
        return array
    }

    private fun decodeApps(array: JSONArray?): List<AppLocaleEntry> {
        if (array == null) return emptyList()
        val out = ArrayList<AppLocaleEntry>(array.length())
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val packageName = obj.optString("packageName", "")
            val languageTag = obj.optString("languageTag", "")
            if (packageName.isNotBlank() && languageTag.isNotBlank()) {
                out.add(AppLocaleEntry(packageName, languageTag))
            }
        }
        return out
    }

    private fun decodeStringList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val out = ArrayList<String>(array.length())
        for (i in 0 until array.length()) {
            val value = array.optString(i, "")
            if (value.isNotBlank()) out.add(value)
        }
        return out
    }
}
