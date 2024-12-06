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
import kotlinx.coroutines.launch
import pw.vintr.vintrless.broadcast.BroadcastController
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.tools.AppActivity
import pw.vintr.vintrless.v2ray.interactor.AndroidV2rayInteractor
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
                    AndroidV2rayInteractor.postConnected()
                }

                BroadcastController.MSG_STATE_CONNECTING -> {
                    AndroidV2rayInteractor.postConnecting()
                }

                BroadcastController.MSG_STATE_NOT_RUNNING,
                BroadcastController.MSG_STATE_START_FAILURE,
                BroadcastController.MSG_STATE_STOP_SUCCESS -> {
                    AndroidV2rayInteractor.postDisconnected()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

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
            AndroidV2rayInteractor.event.collect { event ->
                when (event) {
                    is AndroidV2rayInteractor.Event.StartV2RayViaActivity -> {
                        startV2ray(event.config)
                    }
                    is AndroidV2rayInteractor.Event.StopV2RayViaActivity -> {
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

    private fun stopV2Ray() {
        BroadcastController.sendServiceBroadcast(this, BroadcastController.MSG_STATE_STOP)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
