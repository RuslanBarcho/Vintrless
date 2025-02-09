package pw.vintr.vintrless.v2ray.useCase

import android.content.Context
import android.content.Intent
import android.net.VpnService
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.v2ray.service.V2RayServiceController
import pw.vintr.vintrless.v2ray.storage.AppFilterConfigStorage
import pw.vintr.vintrless.v2ray.storage.V2RayConfigStorage

object V2RayStartUseCase {

    operator fun invoke(
        context: Context,
        config: V2RayEncodedConfig,
        appFilterConfig: ApplicationFilterConfig,
        onPermissionRequestNeed: (Intent) -> Unit = {},
    ) {
        // Save config
        V2RayConfigStorage.saveConfig(context, config)
        AppFilterConfigStorage.saveConfig(context, appFilterConfig)

        // Start service
        val prepareIntent = VpnService.prepare(context)

        if (prepareIntent != null) {
            onPermissionRequestNeed(prepareIntent)
        } else {
            V2RayServiceController.startV2rayService(context = context)
        }
    }
}
