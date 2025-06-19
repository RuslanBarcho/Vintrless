package pw.vintr.vintrless.quickSettings

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import pw.vintr.vintrless.broadcast.V2RayBroadcastReceiver
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.v2ray.storage.AppFilterConfigStorage
import pw.vintr.vintrless.v2ray.storage.V2RayConfigStorage
import pw.vintr.vintrless.v2ray.useCase.V2RayStartUseCase
import pw.vintr.vintrless.v2ray.useCase.V2RayStatusUseCase
import pw.vintr.vintrless.v2ray.useCase.V2RayStopUseCase

class VintrlessQSTileService : TileService() {

    private val receiver = V2RayBroadcastReceiver()
    private var currentState: ConnectionState = ConnectionState.Disconnected
    private var isReceiverRegistered = false

    override fun onCreate() {
        super.onCreate()
        registerReceiver()
    }

    override fun onStartListening() {
        super.onStartListening()
        if (!isReceiverRegistered) {
            registerReceiver()
        }
        syncCurrentState()
    }

    override fun onDestroy() {
        unregisterReceiver()
        super.onDestroy()
    }

    override fun onClick() {
        when (currentState) {
            ConnectionState.Connected,
            ConnectionState.Connecting -> stopVpn()
            else -> startVpn()
        }
    }

    private fun registerReceiver() {
        if (!isReceiverRegistered) {
            receiver.register(
                context = applicationContext,
                listener = object : V2RayBroadcastReceiver.Listener {
                    override fun onConnectionStateChanged(state: ConnectionState) {
                        currentState = state
                        updateTile()
                    }
                },
                sendRegisterEvent = true
            )
            isReceiverRegistered = true
        }
    }

    private fun unregisterReceiver() {
        if (isReceiverRegistered) {
            receiver.unregister(applicationContext)
            isReceiverRegistered = false
        }
    }

    private fun syncCurrentState() {
        currentState = if (V2RayStatusUseCase(applicationContext)) {
            ConnectionState.Connected
        } else {
            ConnectionState.Disconnected
        }
        updateTile()
    }

    private fun startVpn() {
        val config = V2RayConfigStorage.getConfig(applicationContext) ?: run {
            updateTile()
            return
        }

        val appFilterConfig = AppFilterConfigStorage.getConfig(applicationContext)
            ?: ApplicationFilterConfig.empty()

        currentState = ConnectionState.Connecting
        updateTile()

        V2RayStartUseCase(
            context = applicationContext,
            config = config,
            appFilterConfig = appFilterConfig
        )
    }

    private fun stopVpn() {
        currentState = ConnectionState.Disconnected
        updateTile()

        V2RayStopUseCase(applicationContext)
    }

    private fun updateTile() {
        qsTile?.let { tile ->
            val config = V2RayConfigStorage.getConfig(applicationContext)

            tile.label = config?.name ?: applicationInfo.loadLabel(packageManager).toString()

            tile.state = when {
                config == null -> Tile.STATE_INACTIVE
                currentState == ConnectionState.Connected ||
                currentState == ConnectionState.Connecting -> Tile.STATE_ACTIVE
                else -> Tile.STATE_INACTIVE
            }

            tile.updateTile()
        }
    }
}
