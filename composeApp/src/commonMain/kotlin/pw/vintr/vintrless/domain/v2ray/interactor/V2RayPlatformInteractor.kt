package pw.vintr.vintrless.domain.v2ray.interactor

import kotlinx.coroutines.flow.Flow
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig

interface V2RayPlatformInteractor {

    val connectionState: Flow<ConnectionState>

    fun startV2ray(config: V2RayEncodedConfig)

    fun stopV2ray()
}
