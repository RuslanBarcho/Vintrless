package pw.vintr.vintrless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import io.github.vinceglb.filekit.core.FileKit
import kotlinx.coroutines.launch
import pw.vintr.vintrless.broadcast.V2RayBroadcastReceiver
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.tools.AppActivity
import pw.vintr.vintrless.v2ray.interactor.AndroidV2RayInteractor
import pw.vintr.vintrless.v2ray.service.V2RayServiceController
import pw.vintr.vintrless.v2ray.storage.AppFilterConfigStorage
import pw.vintr.vintrless.v2ray.storage.V2RayConfigStorage
import pw.vintr.vintrless.v2ray.useCase.V2RayStartUseCase
import pw.vintr.vintrless.v2ray.useCase.V2RayRestartUseCase
import pw.vintr.vintrless.v2ray.useCase.V2RayStopUseCase

class MainActivity : ComponentActivity() {

    init {
        AppActivity.setUp { this }
    }

    private val requestVpnPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            V2RayServiceController.startV2rayService(context = applicationContext)
        }
    }

    private val v2RayReceiver = V2RayBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        FileKit.init(activity = this)

        registerV2RayReceiver()
        startListenInteractorEvents()

        setContent {
            App()
        }
    }

    private fun registerV2RayReceiver() {
        v2RayReceiver.register(
            context = applicationContext,
            listener = object : V2RayBroadcastReceiver.Listener {
                override fun onConnectionStateChanged(state: ConnectionState) {
                    AndroidV2RayInteractor.postState(state)
                }
            },
            sendRegisterEvent = true,
        )
    }

    private fun startListenInteractorEvents() {
        lifecycleScope.launch {
            AndroidV2RayInteractor.event.collect { event ->
                when (event) {
                    is AndroidV2RayInteractor.Event.StartV2RayViaActivity -> {
                        startV2ray(event.config, event.appFilterConfig)
                    }
                    is AndroidV2RayInteractor.Event.RestartV2RayViaActivity -> {
                        restartV2Ray(event.config, event.appFilterConfig)
                    }
                    is AndroidV2RayInteractor.Event.StopV2RayViaActivity -> {
                        stopV2Ray()
                    }
                    is AndroidV2RayInteractor.Event.ApplyConfigViaActivity -> {
                        applyConfig(event.config, event.appFilterConfig)
                    }
                }
            }
        }
    }

    private fun startV2ray(config: V2RayEncodedConfig, appFilterConfig: ApplicationFilterConfig) {
        V2RayStartUseCase(
            context = applicationContext,
            config = config,
            appFilterConfig = appFilterConfig,
            onPermissionRequestNeed = { requestVpnPermission.launch(it) }
        )
    }

    private fun restartV2Ray(config: V2RayEncodedConfig, appFilterConfig: ApplicationFilterConfig) {
        V2RayRestartUseCase(
            context = applicationContext,
            config = config,
            appFilterConfig = appFilterConfig,
        )
    }

    private fun stopV2Ray() {
        V2RayStopUseCase(applicationContext)
    }

    private fun applyConfig(config: V2RayEncodedConfig, appFilterConfig: ApplicationFilterConfig) {
        V2RayConfigStorage.saveConfig(applicationContext, config)
        AppFilterConfigStorage.saveConfig(applicationContext, appFilterConfig)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
