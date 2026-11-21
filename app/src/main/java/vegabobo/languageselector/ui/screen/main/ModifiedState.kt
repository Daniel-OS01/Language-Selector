package vegabobo.languageselector.ui.screen.main

/** Null means an unavailable/failed read, not a successfully empty locale list. */
internal fun resolveModifiedState(
    previouslyModified: Boolean,
    observedModified: Boolean?,
    hasAccess: Boolean,
): Boolean = hasAccess && (observedModified ?: previouslyModified)
