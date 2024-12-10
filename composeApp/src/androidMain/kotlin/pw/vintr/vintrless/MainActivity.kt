package pw.vintr.vintrless

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import io.github.vinceglb.filekit.core.FileKit
import kotlinx.coroutines.launch
import pw.vintr.vintrless.broadcast.BroadcastController
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.tools.AppActivity
import pw.vintr.vintrless.v2ray.interactor.AndroidV2RayInteractor
import pw.vintr.vintrless.v2ray.service.V2RayServiceController
import pw.vintr.vintrless.v2ray.storage.V2RayConfigStorage

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

    private val mMsgReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when (intent?.getIntExtra(BroadcastController.BROADCAST_KEY, 0)) {
                BroadcastController.MSG_STATE_START_SUCCESS,
                BroadcastController.MSG_STATE_RUNNING -> {
                    AndroidV2RayInteractor.postConnected()
                }

                BroadcastController.MSG_STATE_CONNECTING -> {
                    AndroidV2RayInteractor.postConnecting()
                }

                BroadcastController.MSG_STATE_NOT_RUNNING,
                BroadcastController.MSG_STATE_START_FAILURE,
                BroadcastController.MSG_STATE_STOP_SUCCESS -> {
                    AndroidV2RayInteractor.postDisconnected()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        FileKit.init(activity = this)

        startListenBroadcast()
        startListenInteractorEvents()

        setContent {
            App()
        }
    }

    private fun startListenBroadcast() {
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
        BroadcastController.sendServiceBroadcast(application, BroadcastController.MSG_REGISTER_CLIENT)
    }

    private fun startListenInteractorEvents() {
        lifecycleScope.launch {
            AndroidV2RayInteractor.event.collect { event ->
                when (event) {
                    is AndroidV2RayInteractor.Event.StartV2RayViaActivity -> {
                        startV2ray(event.config)
                    }
                    is AndroidV2RayInteractor.Event.RestartV2RayViaActivity -> {
                        restartV2Ray(event.config)
                    }
                    is AndroidV2RayInteractor.Event.StopV2RayViaActivity -> {
                        stopV2Ray()
                    }
                }
            }
        }
    }

    private fun startV2ray(config: V2RayEncodedConfig) {
        // Save config
        V2RayConfigStorage.saveConfig(applicationContext, config)

        // Start service
        val prepareIntent = VpnService.prepare(applicationContext)

        if (prepareIntent != null) {
            requestVpnPermission.launch(prepareIntent)
        } else {
            V2RayServiceController.startV2rayService(context = applicationContext)
        }
    }

    private fun restartV2Ray(config: V2RayEncodedConfig) {
        // Save config
        V2RayConfigStorage.saveConfig(applicationContext, config)

        // Restart service
        BroadcastController.sendServiceBroadcast(this, BroadcastController.MSG_STATE_RESTART)
    }

    private fun stopV2Ray() {
        BroadcastController.sendServiceBroadcast(this, BroadcastController.MSG_STATE_STOP)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
