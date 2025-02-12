package pw.vintr.vintrless.presentation.screen.logViewer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.log.interactor.LogPlatformInteractor
import pw.vintr.vintrless.domain.log.model.Log
import pw.vintr.vintrless.platform.manager.ShareActionManager
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.NewLine

class LogViewerViewModel(
    navigator: AppNavigator,
    private val logPlatformInteractor: LogPlatformInteractor,
) : BaseViewModel(navigator) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState = logPlatformInteractor.logFlow.mapLatest {
        LogViewerScreenState(
            logs = it.logs
        )
    }.stateInThis(LogViewerScreenState())

    fun openFilter() {

    }

    fun performShare() {
        ShareActionManager.shareText(
            screenState.value.logs.joinToString(String.NewLine)
        )
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
