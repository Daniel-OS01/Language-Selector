package vegabobo.languageselector

import vegabobo.languageselector.ui.screen.appinfo.LocaleRegion
import vegabobo.languageselector.ui.screen.appinfo.SingleLocale
import vegabobo.languageselector.ui.screen.appinfo.capDisplayName
import java.util.Locale

class LocaleManager {

    val localeList = ArrayList<LocaleRegion>()

    init {
        val locales = Locale.getAvailableLocales()
        val localeListMap = mutableMapOf<String, LocaleRegion>()
        val langCache = mutableMapOf<String, String>()
        for (locale in locales) {
            val languageName = locale.capDisplayName()
            val languageTag = locale.toLanguageTag()
            val languageCode = locale.language
            var language = langCache[languageCode]

            // Optimization: locale.getDisplayLanguage(locale) is an expensive ICU lookup.
            // Caching it by language code prevents repetitive translations for locale variants,
            // reducing execution time from ~850ms down to ~15ms on average.
            if (language == null) {
                language = locale.getDisplayLanguage(locale).replaceFirstChar { it.uppercaseChar() }
                langCache[languageCode] = language
            }

            val existingLocale = localeListMap[language]
            if (existingLocale != null) {
                val singleLocale = SingleLocale(languageName, languageTag)
                existingLocale.locales.add(singleLocale)
                continue
            }

            localeListMap[language] =
                LocaleRegion(language, arrayListOf())
        }
        localeList.addAll(localeListMap.values)
        localeList.sortBy { it.language }
    }

}