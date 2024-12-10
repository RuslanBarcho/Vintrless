package pw.vintr.vintrless.v2ray.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.base.InteractorEvent
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayPlatformInteractor
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig

object AndroidV2RayInteractor : BaseInteractor(), V2RayPlatformInteractor {

    sealed class Event : InteractorEvent {
        data class StartV2RayViaActivity(val config: V2RayEncodedConfig) : Event()

        data class RestartV2RayViaActivity(val config: V2RayEncodedConfig) : Event()

        data object StopV2RayViaActivity : Event()
    }

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)

    override val connectionState: Flow<ConnectionState> = _connectionState
        .shareIn(this, started = SharingStarted.Eagerly, replay = 1)

    override val currentState: ConnectionState get() = _connectionState.value

    override fun startV2ray(config: V2RayEncodedConfig) {
        sendEventSync(Event.StartV2RayViaActivity(config))
    }

    override fun restartV2Ray(config: V2RayEncodedConfig) {
        if (_connectionState.value == ConnectionState.Connected) {
            sendEventSync(Event.RestartV2RayViaActivity(config))
        }
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
