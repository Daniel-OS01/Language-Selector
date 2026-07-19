package vegabobo.languageselector.data

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
}
