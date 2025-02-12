package pw.vintr.vintrless.domain.log.interactor

import kotlinx.coroutines.flow.Flow
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.log.model.LogsContainer

abstract class LogPlatformInteractor : BaseInteractor() {

    abstract val logFlow: Flow<LogsContainer>
}
