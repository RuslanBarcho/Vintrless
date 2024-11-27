package pw.vintr.vintrless.domain.alert.interactor

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import pw.vintr.vintrless.domain.alert.model.AlertModel
import pw.vintr.vintrless.domain.alert.model.AlertState
import pw.vintr.vintrless.domain.base.BaseInteractor

class AlertInteractor : BaseInteractor() {

    private val _alertState = MutableStateFlow(value = AlertState())

    val alertState = _alertState
        .shareIn(scope = this, started = SharingStarted.Lazily, replay = 1)

    fun showAlert(alert: AlertModel) {
        _alertState.value = AlertState(alert, show = true)
    }

    fun hideAlert() {
        _alertState.update { it.copy(show = false) }
    }
}
