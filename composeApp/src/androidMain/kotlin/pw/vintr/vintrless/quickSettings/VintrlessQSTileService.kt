package pw.vintr.vintrless.quickSettings

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.tools.extensions.cancelIfActive
import pw.vintr.vintrless.v2ray.interactor.AndroidV2RayInteractor
import pw.vintr.vintrless.v2ray.storage.AppFilterConfigStorage
import pw.vintr.vintrless.v2ray.storage.V2RayConfigStorage

class VintrlessQSTileService : TileService(), CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext = Dispatchers.Main + job

    private var listenVPNStateUpdatesJob: Job? = null

    override fun onStartListening() {
        super.onStartListening()
        launchUpdateJob()
    }

    override fun onStopListening() {
        super.onStopListening()

        listenVPNStateUpdatesJob.cancelIfActive()
    }

    override fun onClick() {
        super.onClick()

        when (qsTile.state) {
            Tile.STATE_ACTIVE -> {
                AndroidV2RayInteractor.stopV2ray()
            }
            Tile.STATE_INACTIVE -> {
                val config = V2RayConfigStorage.getConfig(applicationContext)
                val appFilterConfig = AppFilterConfigStorage.getConfig(applicationContext)

                if (config != null) {
                    AndroidV2RayInteractor.startV2ray(
                        config = config,
                        appFilterConfig = appFilterConfig ?: ApplicationFilterConfig.empty()
                    )
                }
            }
        }
    }

    private fun launchUpdateJob() {
        listenVPNStateUpdatesJob.cancelIfActive()
        listenVPNStateUpdatesJob = launch(
            CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
            }
        ) {
            AndroidV2RayInteractor.connectionState.collectLatest { state ->
                val config = V2RayConfigStorage.getConfig(applicationContext)

                qsTile.label = config?.name ?: applicationInfo
                    .loadLabel(packageManager)
                    .toString()

                qsTile.state = when {
                    config == null -> {
                        Tile.STATE_UNAVAILABLE
                    }
                    state == ConnectionState.Connecting ||
                    state == ConnectionState.Connected -> {
                        Tile.STATE_ACTIVE
                    }
                    else -> {
                        Tile.STATE_INACTIVE
                    }
                }

                qsTile.updateTile()
            }
        }
    }
}
