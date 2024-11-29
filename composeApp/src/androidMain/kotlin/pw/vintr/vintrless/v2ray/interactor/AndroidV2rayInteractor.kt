package pw.vintr.vintrless.v2ray.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.base.InteractorEvent
import pw.vintr.vintrless.domain.v2ray.interactor.V2rayInteractor
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.v2ray.model.V2rayConfig

object AndroidV2rayInteractor : BaseInteractor(), V2rayInteractor {

    sealed class Event : InteractorEvent {
        data object StartV2RayViaActivity : Event()

        data object StopV2RayViaActivity : Event()
    }

    private var lastConfig: V2rayConfig? = null

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)

    override val connectionState: Flow<ConnectionState> = _connectionState
        .shareIn(this, started = SharingStarted.Eagerly, replay = 1)

    override fun startV2ray(config: V2rayConfig) {
        lastConfig = config
        sendEventSync(Event.StartV2RayViaActivity)
    }

    override fun stopV2ray() {
        sendEventSync(Event.StopV2RayViaActivity)
    }

    fun postConnecting() {
        _connectionState.value = ConnectionState.Connecting
    }

    fun postConnected() {
        _connectionState.value = ConnectionState.Connected
    }

    fun postDisconnected() {
        _connectionState.value = ConnectionState.Disconnected
    }
}
