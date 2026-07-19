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
    fun `first available locale for a language is present in its region`() {
        val firstLocale = Locale.getAvailableLocales().first()
        val language = firstLocale.getDisplayLanguage(firstLocale).replaceFirstChar {
            it.uppercaseChar()
        }
        val languageTag = firstLocale.toLanguageTag()

        val region = LocaleManager().localeList.first { it.language == language }

        assertTrue(
            "Expected $languageTag to be present for $language",
            region.locales.any { it.languageTag == languageTag }
        )
    }
}
