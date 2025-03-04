package pw.vintr.vintrless.quickSettings

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import pw.vintr.vintrless.broadcast.V2RayBroadcastReceiver
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.v2ray.interactor.AndroidV2RayInteractor
import pw.vintr.vintrless.v2ray.storage.AppFilterConfigStorage
import pw.vintr.vintrless.v2ray.storage.V2RayConfigStorage
import pw.vintr.vintrless.v2ray.useCase.V2RayStartUseCase
import pw.vintr.vintrless.v2ray.useCase.V2RayStatusUseCase
import pw.vintr.vintrless.v2ray.useCase.V2RayStopUseCase

class VintrlessQSTileService : TileService(), V2RayBroadcastReceiver.Listener {

    private val v2RayReceiver = V2RayBroadcastReceiver()

    override fun onStartListening() {
        super.onStartListening()

        // Update state immediately
        val isServiceRunning = V2RayStatusUseCase(applicationContext)

        updateTile(
            connectionState = if (isServiceRunning) {
                ConnectionState.Connected
            } else {
                AndroidV2RayInteractor.currentState
            },
        )

        // Register service events receiver
        registerReceiver()
    }

    override fun onStopListening() {
        super.onStopListening()

        // Unregister service events receiver
        unregisterReceiver()
    }

    override fun onClick() {
        super.onClick()

        when (qsTile.state) {
            Tile.STATE_ACTIVE -> {
                V2RayStopUseCase(applicationContext)
                updateTile(ConnectionState.Disconnected)
            }
            Tile.STATE_INACTIVE -> {
                val config = V2RayConfigStorage.getConfig(applicationContext)
                val appFilterConfig = AppFilterConfigStorage
                    .getConfig(applicationContext) ?: ApplicationFilterConfig.empty()

                if (config != null) {
                    V2RayStartUseCase(
                        context = applicationContext,
                        config = config,
                        appFilterConfig = appFilterConfig,
                    )
                    updateTile(ConnectionState.Connecting)
                }
            }
        }
    }

    override fun onConnectionStateChanged(state: ConnectionState) {
        updateTile(state)
    }

    private fun registerReceiver() {
        v2RayReceiver.register(
            context = applicationContext,
            listener = this,
        )
    }

    private fun unregisterReceiver() {
        v2RayReceiver.unregister(applicationContext)
    }

    private fun updateTile(connectionState: ConnectionState) {
        val config = V2RayConfigStorage.getConfig(applicationContext)

        qsTile.label = config?.name ?: applicationInfo
            .loadLabel(packageManager)
            .toString()

        qsTile.state = when {
            config == null -> {
                Tile.STATE_UNAVAILABLE
            }
            connectionState == ConnectionState.Connecting ||
            connectionState == ConnectionState.Connected -> {
                Tile.STATE_ACTIVE
            }
            else -> {
                Tile.STATE_INACTIVE
            }
        }

        qsTile.updateTile()
    }
}
