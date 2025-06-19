package pw.vintr.vintrless.v2ray.service

import pw.vintr.vintrless.domain.singbox.model.SingBoxConfig
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig

interface DesktopV2RayService {

    fun startService(v2RayConfig: V2RayEncodedConfig, singBoxConfig: SingBoxConfig)

    fun stopService()
}
