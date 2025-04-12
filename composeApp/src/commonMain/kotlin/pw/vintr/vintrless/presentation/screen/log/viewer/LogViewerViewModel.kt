package pw.vintr.vintrless.presentation.screen.log.viewer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.alert.interactor.AlertInteractor
import pw.vintr.vintrless.domain.alert.model.AlertModel
import pw.vintr.vintrless.domain.log.interactor.LogPlatformInteractor
import pw.vintr.vintrless.domain.log.model.Log
import pw.vintr.vintrless.domain.log.model.LogFilter
import pw.vintr.vintrless.platform.manager.ShareActionManager
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.presentation.screen.log.filter.LogFilterResult
import pw.vintr.vintrless.tools.extensions.NewLine

class LogViewerViewModel(
    navigator: AppNavigator,
    private val logPlatformInteractor: LogPlatformInteractor,
    private val alertInteractor: AlertInteractor,
) : BaseViewModel(navigator) {

    companion object {
        private const val LOGS_DEBOUNCE_MILLIS = 100L
    }

    init {
        logPlatformInteractor.startInheritLogs()
    }

    private val filterFlow = MutableStateFlow(LogFilter())

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val screenState = combine(
        logPlatformInteractor.logFlow,
        filterFlow
    ) { logs, filter ->
        Pair(logs.logs, filter)
    }.debounce(timeoutMillis = LOGS_DEBOUNCE_MILLIS).mapLatest {
        val logs = it.first
        val filter = it.second

        if (filter.isEmpty) {
            LogViewerScreenState(logs)
        } else {
            LogViewerScreenState(
                logs = logs.filter { log ->
                    val queryValid = filter.query.isEmpty() ||
                            log.payload.contains(filter.query, ignoreCase = true)
                    val typeValid = filter.selection[log.type] == true

                    queryValid && typeValid
                }
            )
        }
    }.stateInThis(LogViewerScreenState())

    override fun onCleared() {
        logPlatformInteractor.stopInheritLogs()
        super.onCleared()
    }

    fun onResume() {
        if (!logPlatformInteractor.isLoggingActive) {
            logPlatformInteractor.startInheritLogs()
        }
    }

    fun onPause() {
        if (logPlatformInteractor.isLoggingActive) {
            logPlatformInteractor.stopInheritLogs()
        }
    }

    fun openFilter() {
        val currentFilter = filterFlow.value

        handleResult(LogFilterResult.KEY) {
            navigator.forwardWithResult<LogFilterResult>(
                screen = AppScreen.LogFilter(
                    query = currentFilter.query,
                    selectedTypesOrdinals = currentFilter.selection
                        .filter { it.value }
                        .map { it.key.ordinal }
                ),
                resultKey = LogFilterResult.KEY,
            ) { result ->
                filterFlow.value = LogFilter(
                    query = result.query,
                    selection = result.selection,
                )
            }
        }
    }

    fun performShare() {
        ShareActionManager.shareText(
            screenState.value.logs.joinToString(String.NewLine) { it.payload }
        )
        if (!ShareActionManager.canOpenActionSheet) {
            alertInteractor.showAlert(AlertModel.LogToShareCopied())
        }
    }

    fun performClear() {
        launch(createExceptionHandler()) {
            logPlatformInteractor.clearLogs()
        }
    }
}

data class LogViewerScreenState(
    val logs: List<Log> = listOf()
) {
    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return logs.hashCode()
    }
}
