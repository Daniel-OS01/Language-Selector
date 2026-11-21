package vegabobo.languageselector.data

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocaleBackupCodecTest {

    @Test
    fun `backup json round trips apps pins and presets`() {
        val backup = LocaleBackup(
            schemaVersion = 1,
            exportedAt = 42L,
            apps = listOf(
                AppLocaleEntry("com.example", "en-US"),
                AppLocaleEntry("com.other", "ja"),
            ),
            pinnedLocales = listOf("English,en", "Portuguese, Brazil,pt-BR"),
            presets = listOf(
                LocalePreset(
                    id = "p1",
                    name = "Work",
                    createdAt = 7L,
                    apps = listOf(AppLocaleEntry("com.example", "en-US")),
                )
            ),
        )

        val decoded = LocaleBackupCodec.decodeBackup(LocaleBackupCodec.encodeBackup(backup))

        assertEquals(backup.schemaVersion, decoded.schemaVersion)
        assertEquals(backup.exportedAt, decoded.exportedAt)
        assertEquals(backup.apps, decoded.apps)
        assertEquals(backup.pinnedLocales, decoded.pinnedLocales)
        assertEquals(1, decoded.presets.size)
        assertEquals("Work", decoded.presets[0].name)
        assertEquals("com.example", decoded.presets[0].apps[0].packageName)
        assertEquals("en-US", decoded.presets[0].apps[0].languageTag)
    }

    @Test
    fun `createPreset rejects blank names`() {
        try {
            LocaleBackupCodec.createPreset("   ", emptyList())
            throw AssertionError("Expected blank name to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("blank"))
        }
    }

    @Test
    fun `packagesToClear removes apps absent from snapshot`() {
        val clear = LocaleApplyPlan.packagesToClear(
            currentModified = listOf("a", "b", "c"),
            snapshotPackages = listOf("a", "c"),
        )
        assertEquals(listOf("b"), clear)
    }

    @Test
    fun `decodeBackup maps malformed json to IllegalArgumentException`() {
        try {
            LocaleBackupCodec.decodeBackup("{not-json")
            throw AssertionError("Expected invalid JSON to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("Invalid backup JSON"))
        }
    }

    @Test
    fun `decodePresets skips malformed entries and keeps valid ones`() {
        val presets = JSONArray()
            .put(JSONObject().put("id", "good").put("name", "Good").put("createdAt", 1L))
            .put(JSONObject().put("id", "").put("name", "Missing id"))
            .put(JSONObject().put("name", "No id field"))
            .put("not-an-object")
            .put(
                JSONObject()
                    .put("id", "also-good")
                    .put("name", "Also Good")
                    .put("createdAt", 2L)
                    .put(
                        "apps",
                        JSONArray().put(
                            JSONObject()
                                .put("packageName", "com.example")
                                .put("languageTag", "en")
                        )
                    )
            )

        val decoded = LocaleBackupCodec.decodePresets(presets)

        assertEquals(2, decoded.size)
        assertEquals("good", decoded[0].id)
        assertEquals("Good", decoded[0].name)
        assertEquals("also-good", decoded[1].id)
        assertEquals(1, decoded[1].apps.size)
        assertEquals("com.example", decoded[1].apps[0].packageName)
    }

    @Test
    fun `decodePresets tolerates invalid array payload`() {
        assertEquals(emptyList<LocalePreset>(), LocaleBackupCodec.decodePresets(null))
        assertEquals(emptyList<LocalePreset>(), LocaleBackupCodec.decodePresets(JSONArray()))
    }

    @Test
    fun `decodeBackup rejects oversized json payload`() {
        val huge = "x".repeat(LocaleBackupLimits.MAX_JSON_CHARS + 1)
        try {
            LocaleBackupCodec.decodeBackup(huge)
            throw AssertionError("Expected oversize JSON to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("exceeds"))
        }
    }

    @Test
    fun `decodeBackup rejects too many apps`() {
        val apps = JSONArray()
        repeat(LocaleBackupLimits.MAX_APPS + 1) { i ->
            apps.put(
                JSONObject()
                    .put("packageName", "com.example.app$i")
                    .put("languageTag", "en")
            )
        }
        val json = JSONObject()
            .put("schemaVersion", 1)
            .put("exportedAt", 1L)
            .put("apps", apps)
            .put("pinnedLocales", JSONArray())
            .put("presets", JSONArray())
            .toString()
        try {
            LocaleBackupCodec.decodeBackup(json)
            throw AssertionError("Expected too many apps to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("apps exceed"))
        }
    }

    @Test
    fun `decodeBackup rejects too many presets`() {
        val presets = JSONArray()
        repeat(LocaleBackupLimits.MAX_PRESETS + 1) { i ->
            presets.put(
                JSONObject()
                    .put("id", "id$i")
                    .put("name", "Preset $i")
                    .put("createdAt", i.toLong())
            )
        }
        val json = JSONObject()
            .put("schemaVersion", 1)
            .put("exportedAt", 1L)
            .put("apps", JSONArray())
            .put("pinnedLocales", JSONArray())
            .put("presets", presets)
            .toString()
        try {
            LocaleBackupCodec.decodeBackup(json)
            throw AssertionError("Expected too many presets to fail")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("presets exceed"))
        }
    }
}
