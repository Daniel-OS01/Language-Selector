package vegabobo.languageselector

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class LocaleManagerTest {

    @Test
    fun `every language region includes at least one locale`() {
        val manager = LocaleManager()

        assertFalse(manager.localeList.isEmpty())
        manager.localeList.forEach { region ->
            assertTrue(
                "Language ${region.language} should include at least one locale",
                region.locales.isNotEmpty()
            )
        }
    }

    @Test
    fun `every available locale is present in its language region`() {
        val regionsByLanguage = LocaleManager().localeList.associateBy { it.language }

        Locale.getAvailableLocales().forEach { locale ->
            val language = locale.getDisplayLanguage(locale).replaceFirstChar {
                it.uppercaseChar()
            }
            val languageTag = locale.toLanguageTag()
            val region = regionsByLanguage[language]
            assertTrue(
                "Expected a region for language $language",
                region != null
            )
            assertTrue(
                "Expected $languageTag to be present for $language",
                region!!.locales.any { it.languageTag == languageTag }
            )
        }
    }
}
