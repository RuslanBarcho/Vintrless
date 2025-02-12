package pw.vintr.vintrless.log

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import pw.vintr.vintrless.domain.log.interactor.LogPlatformInteractor
import pw.vintr.vintrless.domain.log.model.LogsContainer
import pw.vintr.vintrless.tools.list.boundedListOf

class JVMLogInteractor : LogPlatformInteractor() {

    companion object {
        private const val MAX_LOG_BUFFER = 10000
    }

    private val _logFlow = MutableStateFlow(LogsContainer(logs = boundedListOf(MAX_LOG_BUFFER)))

    override val logFlow = _logFlow
        .shareIn(this, started = SharingStarted.WhileSubscribed(), replay = 1)
}
