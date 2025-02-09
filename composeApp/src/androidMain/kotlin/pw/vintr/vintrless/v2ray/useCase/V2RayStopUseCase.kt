package pw.vintr.vintrless.v2ray.useCase

import android.content.Context
import pw.vintr.vintrless.broadcast.BroadcastController

object V2RayStopUseCase {

    operator fun invoke(context: Context) {
        BroadcastController.sendServiceBroadcast(context, BroadcastController.MSG_STATE_STOP)
    }
}
