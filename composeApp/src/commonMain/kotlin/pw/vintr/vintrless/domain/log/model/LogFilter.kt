package pw.vintr.vintrless.domain.log.model

import pw.vintr.vintrless.tools.extensions.Empty

data class LogFilter(
    val query: String = String.Empty,
    val selection: Map<LogType, Boolean> = defaultTypeSelection
) {
    companion object {
        val defaultTypeSelection = mapOf(
            LogType.INFORMATION to true,
            LogType.ERROR to true,
            LogType.WARNING to true,
        )
    }

    val isEmpty = query.isEmpty() && (selection.isEmpty() || selection.all { it.value })
}
