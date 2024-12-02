package pw.vintr.vintrless.v2ray

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.v2ray.interactor.V2rayPlatformInteractor
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig

object CupertinoV2rayInteractor : BaseInteractor(), V2rayPlatformInteractor {

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)

    override val connectionState: Flow<ConnectionState> = _connectionState
        .shareIn(this, started = SharingStarted.Eagerly, replay = 1)

    override fun startV2ray(config: V2RayEncodedConfig) {
        _connectionState.value = ConnectionState.Connecting
    }

    override fun stopV2ray() {
        _connectionState.value = ConnectionState.Disconnected
    }
}
