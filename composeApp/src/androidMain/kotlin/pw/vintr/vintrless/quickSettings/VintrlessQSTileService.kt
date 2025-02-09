package pw.vintr.vintrless.quickSettings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat
import pw.vintr.vintrless.broadcast.BroadcastController
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.v2ray.interactor.AndroidV2RayInteractor
import pw.vintr.vintrless.v2ray.storage.AppFilterConfigStorage
import pw.vintr.vintrless.v2ray.storage.V2RayConfigStorage
import pw.vintr.vintrless.v2ray.useCase.V2RayStartUseCase
import pw.vintr.vintrless.v2ray.useCase.V2RayStatusUseCase
import pw.vintr.vintrless.v2ray.useCase.V2RayStopUseCase

class VintrlessQSTileService : TileService() {

    private val mMsgReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when (intent?.getIntExtra(BroadcastController.BROADCAST_KEY, 0)) {
                BroadcastController.MSG_STATE_START_SUCCESS,
                BroadcastController.MSG_STATE_RUNNING -> {
                    updateTile(ConnectionState.Connected)
                }

                BroadcastController.MSG_STATE_CONNECTING -> {
                    updateTile(ConnectionState.Connecting)
                }

                BroadcastController.MSG_STATE_NOT_RUNNING,
                BroadcastController.MSG_STATE_START_FAILURE,
                BroadcastController.MSG_STATE_STOP_SUCCESS -> {
                    updateTile(ConnectionState.Disconnected)
                }
            }
        }
    }

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

    private fun registerReceiver() {
        val mFilter = IntentFilter(BroadcastController.BROADCAST_ACTION_UI)
        ContextCompat.registerReceiver(
            application,
            mMsgReceiver,
            mFilter,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.RECEIVER_EXPORTED
            } else {
                ContextCompat.RECEIVER_NOT_EXPORTED
            }
        )
    }

    private fun unregisterReceiver() {
        applicationContext.unregisterReceiver(mMsgReceiver)
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
