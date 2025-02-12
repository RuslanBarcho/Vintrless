package pw.vintr.vintrless.presentation.screen.logViewer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import pw.vintr.vintrless.domain.log.interactor.LogPlatformInteractor
import pw.vintr.vintrless.domain.log.model.Log
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator

class LogViewerViewModel(
    navigator: AppNavigator,
    logPlatformInteractor: LogPlatformInteractor,
) : BaseViewModel(navigator) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState = logPlatformInteractor.logFlow.mapLatest {
        LogViewerScreenState(
            logs = it.logs
        )
    }.stateInThis(LogViewerScreenState())
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
