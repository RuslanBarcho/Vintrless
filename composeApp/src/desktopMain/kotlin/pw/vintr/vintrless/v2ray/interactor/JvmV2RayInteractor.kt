package pw.vintr.vintrless.v2ray.interactor

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.singbox.useCase.SingBoxConfigBuildUseCase
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayPlatformInteractor
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.v2ray.service.WindowsV2RayService

object JvmV2RayInteractor : BaseInteractor(), V2RayPlatformInteractor {

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)

    override val connectionState: Flow<ConnectionState> = _connectionState
        .shareIn(this, started = SharingStarted.Eagerly, replay = 1)

    override val currentState: ConnectionState get() = _connectionState.value

    override fun startV2ray(config: V2RayEncodedConfig, appFilterConfig: ApplicationFilterConfig) {
        WindowsV2RayService.startService(config, SingBoxConfigBuildUseCase(appFilterConfig))
    }

    override fun restartV2Ray(config: V2RayEncodedConfig, appFilterConfig: ApplicationFilterConfig) {
        launch {
            WindowsV2RayService.stopService()
            delay(200)
            WindowsV2RayService.startService(config, SingBoxConfigBuildUseCase(appFilterConfig))
        }
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
