package pw.vintr.vintrless.v2ray.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.*
import android.os.Build
import android.os.ParcelFileDescriptor
import android.os.StrictMode
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import pw.vintr.vintrless.v2ray.storage.AppFilterConfigStorage
import pw.vintr.vintrless.v2ray.storage.V2RayConfigStorage
import java.io.File
import java.lang.ref.SoftReference

class V2RayVpnService : VpnService(), V2RayServiceDialog {

    companion object {
        var isActive: Boolean = false

        private const val VPN_MTU = 1500

        private const val PRIVATE_VLAN4_CLIENT = "26.26.26.1"
        private const val PRIVATE_VLAN4_ROUTER = "26.26.26.2"
        private const val PRIVATE_VLAN6_CLIENT = "da26:2626::1"
        private const val PRIVATE_VLAN6_ROUTER = "da26:2626::2"

        private const val TUN2SOCKS = "libtun2socks.so"
        private const val TAG = "V2rayVPNService"

        fun newInstance(context: Context): Intent {
            return Intent(context, V2RayVpnService::class.java)
        }
    }

    private var isRunning = false

    private val logging = logging(tag = TAG)

    private lateinit var mInterface: ParcelFileDescriptor

    private lateinit var process: Process

    @delegate:RequiresApi(Build.VERSION_CODES.P)
    private val defaultNetworkRequest by lazy {
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .build()
    }

    private val connectivity by lazy { getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager }

    @delegate:RequiresApi(Build.VERSION_CODES.P)
    private val defaultNetworkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                setUnderlyingNetworks(arrayOf(network))
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                // it's a good idea to refresh capabilities
                setUnderlyingNetworks(arrayOf(network))
            }

            override fun onLost(network: Network) {
                setUnderlyingNetworks(null)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build()
        )
        V2RayServiceController.serviceDialog = SoftReference(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        V2RayServiceController.startV2rayPoint()
        return START_STICKY
    }

    override fun onRevoke() {
        stopV2Ray()
    }

    private fun setup() {
        val config = V2RayConfigStorage.getConfig(applicationContext)
        val appFilterConfig = AppFilterConfigStorage.getConfig(applicationContext)

        val prepare = prepare(this)
        if (prepare != null) {
            return
        }

        val builder = Builder()

        builder.setMtu(VPN_MTU)
        builder.addAddress(PRIVATE_VLAN4_CLIENT, 30)

//        val bypassLan = SettingsManager.routingRulesetsBypassLan()
//        if (bypassLan) {
//            resources.getStringArray(R.array.bypass_private_ip_address).forEach {
//                val addr = it.split('/')
//                builder.addRoute(addr[0], addr[1].toInt())
//            }
//        } else {
//            builder.addRoute("0.0.0.0", 0)
//        }

        builder.addRoute("0.0.0.0", 0)

//        if (MmkvManager.decodeSettingsBool(AppConfig.PREF_PREFER_IPV6) == true) {
//            builder.addAddress(PRIVATE_VLAN6_CLIENT, 126)
//            if (bypassLan) {
//                builder.addRoute("2000::", 3) //currently only 1/8 of total ipV6 is in use
//            } else {
//                builder.addRoute("::", 0)
//            }
//        }

//        Utils.getVpnDnsServers()
//            .forEach {
//                if (Utils.isPureIpAddress(it)) {
//                    builder.addDnsServer(it)
//                }
//            }
        builder.addDnsServer("1.1.1.1")

        builder.setSession(config?.name ?: "Vpn Service")

        val selfPackageName = "pw.vintr.vintrless"

        // Apply application filter config.
        if (appFilterConfig != null && appFilterConfig.enabled) {
            if (appFilterConfig.isBypass) {
                builder.addDisallowedApplication(selfPackageName)
                appFilterConfig.keys.forEach {
                    builder.tryAddDisallowedApplication(it)
                }
            } else {
                appFilterConfig.keys.forEach {
                    builder.tryAddAllowedApplication(it)
                }
            }
        } else {
            builder.addDisallowedApplication(selfPackageName)
        }

        // Close the old interface since the parameters have been changed.
        try {
            mInterface.close()
        } catch (ignored: Exception) {
            // ignored
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                connectivity.requestNetwork(defaultNetworkRequest, defaultNetworkCallback)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setMetered(false)
//            if (MmkvManager.decodeSettingsBool(AppConfig.PREF_APPEND_HTTP_PROXY)) {
//                builder.setHttpProxy(ProxyInfo.buildDirectProxy(LOOPBACK, SettingsManager.getHttpPort()))
//            }
        }

        // Create a new interface using the builder and save the parameters.
        try {
            mInterface = builder.establish()!!

            isRunning = true
            isActive = true

            runTun2socks()
        } catch (e: Exception) {
            e.printStackTrace()
            stopV2Ray()
        }
    }

    private fun runTun2socks() {
        val socksPort = 10808
        val cmd = arrayListOf(
            File(applicationContext.applicationInfo.nativeLibraryDir, TUN2SOCKS).absolutePath,
            "--netif-ipaddr", PRIVATE_VLAN4_ROUTER,
            "--netif-netmask", "255.255.255.252",
            "--socks-server-addr", "127.0.0.1:${socksPort}",
            "--tunmtu", VPN_MTU.toString(),
            "--sock-path", "sock_path",
            "--enable-udprelay",
            "--loglevel", "notice"
        )

        logging.debug { cmd.toString() }

        try {
            val proBuilder = ProcessBuilder(cmd)
            proBuilder.redirectErrorStream(true)
            process = proBuilder
                .directory(applicationContext.filesDir)
                .start()
            Thread {
                logging.debug { "$TUN2SOCKS check" }
                process.waitFor()
                logging.debug { "$TUN2SOCKS exited" }
                if (isRunning) {
                    logging.debug { "$TUN2SOCKS restart" }
                    runTun2socks()
                }
            }.start()

            logging.debug { process.toString() }

            sendFd()
        } catch (e: Exception) {
            logging.error { e.toString() }
        }
    }

    private fun sendFd() {
        val fd = mInterface.fileDescriptor
        val path = File(applicationContext.filesDir, "sock_path").absolutePath

        logging.debug { path }

        CoroutineScope(Dispatchers.IO).launch {
            var tries = 0
            while (true) try {
                Thread.sleep(50L shl tries)
                logging.debug { "sendFd tries: $tries" }
                LocalSocket().use { localSocket ->
                    localSocket.connect(LocalSocketAddress(path, LocalSocketAddress.Namespace.FILESYSTEM))
                    localSocket.setFileDescriptorsForSend(arrayOf(fd))
                    localSocket.outputStream.write(42)
                }
                break
            } catch (e: Exception) {
                logging.error { e.toString() }
                if (tries > 5) break
                tries += 1
            }
        }
    }

    private fun stopV2Ray() {
        isRunning = false
        isActive = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                connectivity.unregisterNetworkCallback(defaultNetworkCallback)
            } catch (ignored: Exception) {
                // ignored
            }
        }

        try {
            logging.debug { "tun2socks destroy" }
            process.destroy()
        } catch (e: Exception) {
            logging.error { e.toString() }
        }

        V2RayServiceController.stopV2rayPoint()

        stopSelf()

        try {
            mInterface.close()
        } catch (e: Exception) {
            logging.error { e.toString() }
        }
    }

    override fun getService(): Service {
        return this
    }

    override fun startService() {
        setup()
    }

    override fun stopService() {
        stopV2Ray()
    }

    override fun vpnProtect(socket: Int): Boolean {
        return protect(socket)
    }

    private fun Builder.tryAddDisallowedApplication(packageName: String) {
        try {
            addDisallowedApplication(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun Builder.tryAddAllowedApplication(packageName: String) {
        try {
            addAllowedApplication(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}
