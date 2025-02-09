package pw.vintr.vintrless.v2ray.useCase

import android.app.ActivityManager
import android.content.Context
import pw.vintr.vintrless.v2ray.service.V2RayVpnService

object V2RayStatusUseCase {

    @Suppress("DEPRECATION")
    operator fun invoke(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = manager.getRunningServices(Integer.MAX_VALUE)

        for (service in runningServices) {
            if (service.service.className == V2RayVpnService::class.java.name) {
                return true
            }
        }
        return false
    }
}
