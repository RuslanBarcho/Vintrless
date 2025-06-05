package pw.vintr.vintrless.v2ray.service

import kotlinx.coroutines.*
import pw.vintr.vintrless.domain.log.JVMLogInteractor
import pw.vintr.vintrless.domain.log.model.LogType
import pw.vintr.vintrless.domain.singbox.model.SingBoxConfig
import pw.vintr.vintrless.domain.system.interactor.SystemInteractor
import pw.vintr.vintrless.domain.system.model.SudoPasswordRequestReason
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.tools.PathProvider
import pw.vintr.vintrless.tools.extensions.addShutdownHook
import pw.vintr.vintrless.tools.extensions.close
import pw.vintr.vintrless.v2ray.interactor.JvmV2RayInteractor
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*

object MacosV2RayService : DesktopV2RayService {

    enum class ProcType {
        TUN,
        XRAY
    }

    private const val PROCESS_START_DELAY_MILLIS = 300L

    private val runningProcMap: MutableMap<ProcType, Process> = mutableMapOf()

    private val resourcesDir = File(PathProvider.resourcesPath)

    private var serviceStartJob: Job? = null

    override fun startService(v2RayConfig: V2RayEncodedConfig, singBoxConfig: SingBoxConfig) {
        serviceStartJob = MainScope().launch {
            val password = SystemInteractor.getSudoPassword(SudoPasswordRequestReason.START_TUN)

            if (password != null) {
                JvmV2RayInteractor.postConnecting()

                try {
                    // Save configs to files
                    saveConfig(
                        path = "${PathProvider.resourcesPath}/config_xray.json",
                        json = v2RayConfig.configJson
                    )
                    saveConfig(
                        path = "${PathProvider.resourcesPath}/config_singbox.json",
                        json = singBoxConfig.toJson()
                    )

                    // Start xray listener
                    val xrayProc = ProcessBuilder("./xray", "run", "-c", "config_xray.json")
                        .directory(resourcesDir)
                        .start()

                    inheritIO(xrayProc.inputStream, System.out)
                    inheritIO(xrayProc.errorStream, System.err)
                    xrayProc.addShutdownHook()

                    runningProcMap[ProcType.XRAY] = xrayProc

                    delay(PROCESS_START_DELAY_MILLIS)

                    val tunProc = ProcessBuilder(
                        "sudo", "-S",
                        "env", "ENABLE_DEPRECATED_TUN_ADDRESS_X=true",
                        "./sing-box", "run", "-c", "config_singbox.json"
                    )
                        .directory(resourcesDir)
                        .start()

                    tunProc.outputStream.bufferedWriter().use {
                        it.write("$password\n")
                        it.flush()
                    }

                    inheritIO(tunProc.inputStream, System.out)
                    inheritIO(tunProc.errorStream, System.err)
                    tunProc.addShutdownHook()

                    runningProcMap[ProcType.TUN] = tunProc

                    // Notify UI
                    JvmV2RayInteractor.postConnected()
                } catch (exception: Throwable) {
                    exception.printStackTrace()

                    stopService()
                    JvmV2RayInteractor.postDisconnected()
                }
            }
        }
    }

    private fun saveConfig(path: String, json: String) {
        val configFile = File(path)

        // Remove current config if exist
        if (configFile.exists()) {
            configFile.delete()
            configFile.createNewFile()
        }

        // Write new config content
        val fileWriter = FileWriter(configFile.absoluteFile)
        val bufferedWriter = BufferedWriter(fileWriter)

        bufferedWriter.write(json.replace("\\", "/"))
        bufferedWriter.close()
    }

    private fun inheritIO(src: InputStream, dest: PrintStream) {
        Thread {
            val sc = Scanner(src)
            while (sc.hasNextLine()) {
                val message = String(sc.nextLine().toByteArray(), StandardCharsets.UTF_8)

                dest.println(message)
                JVMLogInteractor.pushLog(
                    message = message,
                    type = when {
                        dest == System.err ||
                                message.contains("failed", ignoreCase = true) -> {
                            LogType.ERROR
                        }
                        message.contains("warning", ignoreCase = true) -> {
                            LogType.WARNING
                        }
                        else -> {
                            LogType.INFORMATION
                        }
                    }
                )
            }
        }.start()
    }

    override fun stopService() {
        runningProcMap[ProcType.TUN]?.close()
        runningProcMap[ProcType.XRAY]?.close()

        runningProcMap.clear()
        JvmV2RayInteractor.postDisconnected()
    }
}
