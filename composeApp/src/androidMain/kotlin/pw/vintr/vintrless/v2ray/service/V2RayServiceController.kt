package pw.vintr.vintrless.v2ray.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import go.Seq
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import libv2ray.Libv2ray
import libv2ray.V2RayPoint
import libv2ray.V2RayVPNServiceSupportsSet
import org.lighthousegames.logging.logging
import pw.vintr.vintrless.MainActivity
import pw.vintr.vintrless.R
import pw.vintr.vintrless.broadcast.V2RayBroadcastController
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.tools.AppContext
import pw.vintr.vintrless.tools.extensions.Empty
import pw.vintr.vintrless.v2ray.storage.V2RayConfigStorage
import java.lang.ref.SoftReference

object V2RayServiceController {

    private const val RESTART_SERVICE_DELAY_MILLIS = 500L

    private const val NOTIFICATION_ID = 1
    private const val NOTIFICATION_PENDING_INTENT_CONTENT = 0
    private const val NOTIFICATION_PENDING_INTENT_STOP_V2RAY = 1
    private const val NOTIFICATION_PENDING_INTENT_RESTART_V2RAY = 2

    private const val NOTIFICATION_CHANNEL_ID = "VintrlessV2RayServiceNotification"
    private const val NOTIFICATION_CHANNEL_NAME = "Vintrless Background Service"

    private const val DIR_ASSETS = "assets"
    private const val TAG = "V2RayServiceController"

    private val logging = logging(tag = TAG)

    private val v2rayPoint: V2RayPoint = Libv2ray.newV2RayPoint(
        V2RayCallback(),
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    )

    private val mMsgReceive = ReceiveMessageHandler()

    private var mNotificationManager: NotificationManager? = null

    var serviceDialog: SoftReference<V2RayServiceDialog>? = null
        set(value) {
            field = value
            Seq.setContext(value?.get()?.getService()?.applicationContext)

            val service = value?.get()?.getService()
            val assetsPath = service?.getExternalFilesDir(DIR_ASSETS)?.absolutePath
                ?: service?.getDir(DIR_ASSETS, 0)?.absolutePath

            val androidId = Settings.Secure.ANDROID_ID.toByteArray(Charsets.UTF_8)
            val deviceIdForXUDPBaseKey = Base64.encodeToString(
                androidId.copyOf(32),
                Base64.NO_PADDING.or(Base64.URL_SAFE)
            )

            Libv2ray.initV2Env(assetsPath, deviceIdForXUDPBaseKey)
        }

    private class V2RayCallback : V2RayVPNServiceSupportsSet {
        override fun shutdown(): Long {
            val serviceControl = serviceDialog?.get() ?: return -1
            // called by go
            return try {
                serviceControl.stopService()
                0
            } catch (e: Exception) {
                logging.error { e.toString() }
                -1
            }
        }

        override fun prepare(): Long {
            return 0
        }

        override fun protect(l: Long): Boolean {
            val serviceControl = serviceDialog?.get() ?: return true
            return serviceControl.vpnProtect(l.toInt())
        }

        override fun onEmitStatus(l: Long, s: String?): Long {
            return 0
        }

        override fun setup(s: String): Long {
            val serviceControl = serviceDialog?.get() ?: return -1
            return try {
                serviceControl.startService()
                0
            } catch (e: Exception) {
                logging.error { e.toString() }
                -1
            }
        }
    }

    private class ReceiveMessageHandler : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val serviceControl = serviceDialog?.get() ?: return
            when (intent?.getIntExtra(V2RayBroadcastController.BROADCAST_KEY, 0)) {
                V2RayBroadcastController.MSG_REGISTER_CLIENT -> {
                    if (v2rayPoint.isRunning) {
                        V2RayBroadcastController.sendUIBroadcast(
                            serviceControl.getService(),
                            V2RayBroadcastController.MSG_STATE_RUNNING
                        )
                    } else {
                        V2RayBroadcastController.sendUIBroadcast(
                            serviceControl.getService(),
                            V2RayBroadcastController.MSG_STATE_NOT_RUNNING
                        )
                    }
                }

                V2RayBroadcastController.MSG_UNREGISTER_CLIENT -> {}

                V2RayBroadcastController.MSG_STATE_START -> {}

                V2RayBroadcastController.MSG_STATE_STOP -> {
                    logging.debug { "Stopping Service" }

                    serviceControl.stopService()
                }

                V2RayBroadcastController.MSG_STATE_RESTART -> {
                    logging.debug { "Restarting Service" }

                    serviceControl.stopService()
                    Thread.sleep(RESTART_SERVICE_DELAY_MILLIS)
                    startV2rayService(serviceControl.getService())
                }
            }
        }
    }

    fun startV2rayService(context: Context) {
        V2RayBroadcastController.sendUIBroadcast(AppContext.get(), V2RayBroadcastController.MSG_STATE_CONNECTING)

        val intent = V2RayVpnService.newInstance(context)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun startV2rayPoint() {
        val service = serviceDialog?.get()?.getService() ?: return
        val config = V2RayConfigStorage.getConfig(service.applicationContext) ?: return

        logging.debug { "Starting v2ray with config:\n${config.configJson}" }

        if (v2rayPoint.isRunning) {
            return
        }

        try {
            val mFilter = IntentFilter(V2RayBroadcastController.BROADCAST_ACTION_SERVICE)
            mFilter.addAction(Intent.ACTION_SCREEN_ON)
            mFilter.addAction(Intent.ACTION_SCREEN_OFF)
            mFilter.addAction(Intent.ACTION_USER_PRESENT)
            ContextCompat.registerReceiver(
                service,
                mMsgReceive,
                mFilter,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.RECEIVER_EXPORTED
                } else {
                    ContextCompat.RECEIVER_NOT_EXPORTED
                }
            )
        } catch (e: Exception) {
            logging.error { e.toString() }
        }

        v2rayPoint.configureFileContent = config.configJson
        v2rayPoint.domainName = config.domainName

        try {
            v2rayPoint.runLoop(false)
        } catch (e: Exception) {
            logging.error { e.toString() }
        }

        if (v2rayPoint.isRunning) {
            V2RayBroadcastController.sendUIBroadcast(service, V2RayBroadcastController.MSG_STATE_START_SUCCESS)
            showNotification(config)
        } else {
            V2RayBroadcastController.sendUIBroadcast(service, V2RayBroadcastController.MSG_STATE_START_FAILURE)
        }
    }

    fun stopV2rayPoint() {
        val service = serviceDialog?.get()?.getService() ?: return

        if (v2rayPoint.isRunning) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    v2rayPoint.stopLoop()
                } catch (e: Exception) {
                    logging.error { e.toString() }
                }
            }
        }

        V2RayBroadcastController.sendUIBroadcast(service, V2RayBroadcastController.MSG_STATE_STOP_SUCCESS)

        try {
            service.unregisterReceiver(mMsgReceive)
        } catch (e: Exception) {
            logging.error { e.toString() }
        }
    }

    private fun showNotification(config: V2RayEncodedConfig) {
        val service = serviceDialog?.get()?.getService() ?: return
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val startMainIntent = Intent(service, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            service,
            NOTIFICATION_PENDING_INTENT_CONTENT,
            startMainIntent,
            flags
        )

        val stopV2RayIntent = Intent(V2RayBroadcastController.BROADCAST_ACTION_SERVICE)
        stopV2RayIntent.`package` = "pw.vintr.vintrless"
        stopV2RayIntent.putExtra(V2RayBroadcastController.BROADCAST_KEY, V2RayBroadcastController.MSG_STATE_STOP)

        val stopV2RayPendingIntent = PendingIntent.getBroadcast(
            service,
            NOTIFICATION_PENDING_INTENT_STOP_V2RAY,
            stopV2RayIntent,
            flags
        )

        val restartV2RayIntent = Intent(V2RayBroadcastController.BROADCAST_ACTION_SERVICE)
        restartV2RayIntent.`package` = "pw.vintr.vintrless"
        restartV2RayIntent.putExtra(V2RayBroadcastController.BROADCAST_KEY, V2RayBroadcastController.MSG_STATE_RESTART)

        val restartV2RayPendingIntent = PendingIntent.getBroadcast(
            service,
            NOTIFICATION_PENDING_INTENT_RESTART_V2RAY,
            restartV2RayIntent,
            flags
        )

        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        } else {
            String.Empty
        }

        val mBuilder = NotificationCompat.Builder(service, channelId)
            .setSmallIcon(R.drawable.ic_service_notification)
            .setContentTitle(service.getString(R.string.service_connected_to, config.name))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setShowWhen(false)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentPendingIntent)
            .addAction(NotificationCompat.Action(
                null,
                service.getString(R.string.service_stop_action),
                stopV2RayPendingIntent
            ))

        service.startForeground(NOTIFICATION_ID, mBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = NOTIFICATION_CHANNEL_ID
        val channelName = NOTIFICATION_CHANNEL_NAME
        val channel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH
        )
        channel.lightColor = Color.DKGRAY
        channel.importance = NotificationManager.IMPORTANCE_NONE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getNotificationManager()?.createNotificationChannel(channel)
        return channelId
    }

    private fun getNotificationManager(): NotificationManager? {
        if (mNotificationManager == null) {
            val service = serviceDialog?.get()?.getService() ?: return null
            mNotificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mNotificationManager
    }
}
