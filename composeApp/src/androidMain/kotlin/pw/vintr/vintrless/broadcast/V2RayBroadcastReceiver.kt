package pw.vintr.vintrless.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState

class V2RayBroadcastReceiver : BroadcastReceiver() {

    interface Listener {
        fun onConnectionStateChanged(state: ConnectionState) {}
    }

    private var listener: Listener? = null

    override fun onReceive(ctx: Context?, intent: Intent?) {
        when (intent?.getIntExtra(V2RayBroadcastController.BROADCAST_KEY, 0)) {
            V2RayBroadcastController.MSG_STATE_START_SUCCESS,
            V2RayBroadcastController.MSG_STATE_RUNNING -> {
                listener?.onConnectionStateChanged(ConnectionState.Connected)
            }

            V2RayBroadcastController.MSG_STATE_CONNECTING -> {
                listener?.onConnectionStateChanged(ConnectionState.Connecting)
            }

            V2RayBroadcastController.MSG_STATE_NOT_RUNNING,
            V2RayBroadcastController.MSG_STATE_START_FAILURE,
            V2RayBroadcastController.MSG_STATE_STOP_SUCCESS -> {
                listener?.onConnectionStateChanged(ConnectionState.Disconnected)
            }
        }
    }

    fun register(context: Context, listener: Listener, sendRegisterEvent: Boolean = false) {
        this.listener = listener

        val mFilter = IntentFilter(V2RayBroadcastController.BROADCAST_ACTION_UI)
        ContextCompat.registerReceiver(
            context,
            this,
            mFilter,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.RECEIVER_EXPORTED
            } else {
                ContextCompat.RECEIVER_NOT_EXPORTED
            }
        )

        if (sendRegisterEvent) {
            V2RayBroadcastController.sendServiceBroadcast(context, V2RayBroadcastController.MSG_REGISTER_CLIENT)
        }
    }

    fun unregister(context: Context) {
        listener = null
        context.unregisterReceiver(this)
    }
}
