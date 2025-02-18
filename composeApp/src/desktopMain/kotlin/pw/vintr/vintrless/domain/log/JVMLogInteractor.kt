package pw.vintr.vintrless.domain.log

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import pw.vintr.vintrless.domain.log.interactor.LogPlatformInteractor
import pw.vintr.vintrless.domain.log.model.Log
import pw.vintr.vintrless.domain.log.model.LogType
import pw.vintr.vintrless.domain.log.model.LogsContainer
import pw.vintr.vintrless.tools.list.boundedListOf

object JVMLogInteractor : LogPlatformInteractor() {

    private const val MAX_LOG_BUFFER = 10000

    private val _logFlow = MutableStateFlow(LogsContainer(logs = boundedListOf(MAX_LOG_BUFFER)))

    override val logFlow = _logFlow
        .shareIn(this, started = SharingStarted.WhileSubscribed(), replay = 1)

    fun pushLog(
        message: String,
        type: LogType,
    ) {
        _logFlow.value = _logFlow.value.copy(
            logs = _logFlow.value.logs.apply {
                add(Log(payload = message, type = type))
            }
        )
    }
}
