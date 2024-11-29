package pw.vintr.vintrless.v2ray.service

import android.app.Service

interface V2RayServiceDialog {

    fun getService(): Service

    fun startService()

    fun stopService()

    fun vpnProtect(socket: Int): Boolean
}
