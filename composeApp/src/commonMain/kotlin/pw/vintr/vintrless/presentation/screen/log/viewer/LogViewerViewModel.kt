package pw.vintr.vintrless.presentation.screen.log.viewer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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

    private val filterFlow = MutableStateFlow(LogFilter())

    val screenState = combine(
        logPlatformInteractor.logFlow,
        filterFlow
    ) { logs, filter ->
        if (filter.isEmpty) {
            LogViewerScreenState(logs.logs)
        } else {
            LogViewerScreenState(
                logs = logs.logs.filter { log ->
                    val queryValid = filter.query.isEmpty() ||
                            log.payload.contains(filter.query, ignoreCase = true)
                    val typeValid = filter.selection[log.type] == true

                    queryValid && typeValid
                }
            )
        }
    }.stateInThis(LogViewerScreenState())

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
