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
        val languageCache = mutableMapOf<String, String>()
        for (locale in locales) {
            val languageName = locale.capDisplayName()
            val languageTag = locale.toLanguageTag()
            val language = languageCache.getOrPut(locale.language) {
                locale.getDisplayLanguage(locale).replaceFirstChar { it.uppercaseChar() }
            }

            val singleLocale = SingleLocale(languageName, languageTag)
            val existingLocale = localeListMap[language]
            if (existingLocale != null) {
                existingLocale.locales.add(singleLocale)
                continue
            }

            localeListMap[language] =
                LocaleRegion(language, arrayListOf(singleLocale))
        }
        localeList.addAll(localeListMap.values)
        localeList.sortBy { it.language }
    }

}