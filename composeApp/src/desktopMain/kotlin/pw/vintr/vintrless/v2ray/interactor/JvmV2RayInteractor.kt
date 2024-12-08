package pw.vintr.vintrless.v2ray.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayPlatformInteractor
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.v2ray.service.WindowsV2RayService

object JvmV2RayInteractor : BaseInteractor(), V2RayPlatformInteractor {

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)

    override val connectionState: Flow<ConnectionState> = _connectionState
        .shareIn(this, started = SharingStarted.Eagerly, replay = 1)

    override fun startV2ray(config: V2RayEncodedConfig) {
        WindowsV2RayService.startService(config)
    }

    override fun stopV2ray() {
        WindowsV2RayService.stopService()
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
