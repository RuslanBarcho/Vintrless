package pw.vintr.vintrless.broadcast

import android.content.Context
import android.content.Intent
import pw.vintr.vintrless.tools.extensions.Empty
import java.io.Serializable

object BroadcastController {

    /** Message constants for communication. */
    const val MSG_REGISTER_CLIENT = 1
    const val MSG_STATE_RUNNING = 11
    const val MSG_STATE_NOT_RUNNING = 12
    const val MSG_UNREGISTER_CLIENT = 2
    const val MSG_STATE_START = 3
    const val MSG_STATE_START_SUCCESS = 31
    const val MSG_STATE_START_FAILURE = 32
    const val MSG_STATE_STOP = 4
    const val MSG_STATE_STOP_SUCCESS = 41
    const val MSG_STATE_RESTART = 5
    const val MSG_STATE_CONNECTING = 6

    const val BROADCAST_ACTION_SERVICE = "pw.vintr.vintrless.action.service"
    const val BROADCAST_ACTION_UI = "pw.vintr.vintrless.action.ui"

    const val BROADCAST_KEY = "key"

    fun sendServiceBroadcast(ctx: Context, what: Int, content: Serializable = "") {
        sendBroadcast(ctx, BROADCAST_ACTION_SERVICE, what, content)
    }

    fun sendUIBroadcast(ctx: Context, what: Int, content: Serializable = "") {
        sendBroadcast(ctx, BROADCAST_ACTION_UI, what, content)
    }

    private fun sendBroadcast(ctx: Context, action: String, what: Int, content: Serializable) {
        try {
            val intent = Intent()
            intent.action = action
            intent.`package` = "pw.vintr.vintrless"
            intent.putExtra("key", what)
            intent.putExtra("content", content)
            ctx.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
