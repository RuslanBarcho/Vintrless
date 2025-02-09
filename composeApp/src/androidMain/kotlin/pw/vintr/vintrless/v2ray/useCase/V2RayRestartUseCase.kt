package pw.vintr.vintrless.v2ray.useCase

import android.content.Context
import pw.vintr.vintrless.broadcast.BroadcastController
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.v2ray.storage.AppFilterConfigStorage
import pw.vintr.vintrless.v2ray.storage.V2RayConfigStorage

object V2RayRestartUseCase {

    operator fun invoke(
        context: Context,
        config: V2RayEncodedConfig,
        appFilterConfig: ApplicationFilterConfig,
    ) {
        // Save config
        V2RayConfigStorage.saveConfig(context, config)
        AppFilterConfigStorage.saveConfig(context, appFilterConfig)

        // Restart service
        BroadcastController.sendServiceBroadcast(context, BroadcastController.MSG_STATE_RESTART)
    }
}
