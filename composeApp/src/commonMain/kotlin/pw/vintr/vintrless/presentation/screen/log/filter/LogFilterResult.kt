package pw.vintr.vintrless.presentation.screen.log.filter

import pw.vintr.vintrless.domain.log.model.LogType
import pw.vintr.vintrless.tools.extensions.Empty

data class LogFilterResult(
    val query: String = String.Empty,
    val selection: Map<LogType, Boolean>,
) {
    companion object {
        const val KEY = "log-filter-apply-result"
    }
}
