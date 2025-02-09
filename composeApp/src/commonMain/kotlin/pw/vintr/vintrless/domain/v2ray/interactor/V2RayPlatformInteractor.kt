package pw.vintr.vintrless.domain.v2ray.interactor

import kotlinx.coroutines.flow.Flow
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig

interface V2RayPlatformInteractor {

    val connectionState: Flow<ConnectionState>

    val currentState: ConnectionState

    fun startV2ray(config: V2RayEncodedConfig, appFilterConfig: ApplicationFilterConfig)

    fun restartV2Ray(config: V2RayEncodedConfig, appFilterConfig: ApplicationFilterConfig)

    fun stopV2ray()

    fun applyConfig(config: V2RayEncodedConfig, appFilterConfig: ApplicationFilterConfig) = Unit
}
