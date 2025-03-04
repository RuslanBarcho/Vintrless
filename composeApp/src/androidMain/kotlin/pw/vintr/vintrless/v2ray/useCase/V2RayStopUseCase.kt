package pw.vintr.vintrless.v2ray.useCase

import android.content.Context
import pw.vintr.vintrless.broadcast.V2RayBroadcastController

object V2RayStopUseCase {

    operator fun invoke(context: Context) {
        V2RayBroadcastController.sendServiceBroadcast(context, V2RayBroadcastController.MSG_STATE_STOP)
    }
}
