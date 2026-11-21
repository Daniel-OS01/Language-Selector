package vegabobo.languageselector.data

/** A failed read must never turn a partial snapshot into a destructive empty backup. */
internal fun collectLocaleSnapshot(
    packages: List<String>,
    readLanguageTag: (String) -> String?,
): List<AppLocaleEntry> = packages.mapNotNull { pkg ->
    val tag = readLanguageTag(pkg)
        ?: error("Could not read application language for $pkg")
    if (tag.isBlank()) null else AppLocaleEntry(pkg, tag)
}.sortedBy { it.packageName }
