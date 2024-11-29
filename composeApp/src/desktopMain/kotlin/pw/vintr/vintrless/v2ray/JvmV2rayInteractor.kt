package pw.vintr.vintrless.v2ray

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.v2ray.interactor.V2rayInteractor
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.v2ray.model.V2rayConfig

object JvmV2rayInteractor : BaseInteractor(), V2rayInteractor {

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)

    override val connectionState: Flow<ConnectionState> = _connectionState
        .shareIn(this, started = SharingStarted.Eagerly, replay = 1)

    override fun startV2ray(config: V2rayConfig) {
        _connectionState.value = ConnectionState.Connecting
    }

    override fun stopV2ray() {
        _connectionState.value = ConnectionState.Disconnected
    }
}
