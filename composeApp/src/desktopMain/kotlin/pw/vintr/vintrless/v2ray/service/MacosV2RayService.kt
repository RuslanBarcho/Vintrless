package pw.vintr.vintrless.v2ray.service

import kotlinx.coroutines.*
import pw.vintr.vintrless.domain.singbox.model.SingBoxConfig
import pw.vintr.vintrless.domain.system.interactor.SystemInteractor
import pw.vintr.vintrless.domain.system.model.SudoPasswordRequestReason
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.tools.PathProvider
import pw.vintr.vintrless.tools.exception.WrongSudoPasswordException
import pw.vintr.vintrless.tools.extensions.*
import pw.vintr.vintrless.v2ray.interactor.JvmV2RayInteractor
import java.io.*

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

                    inheritProcessIO(xrayProc.inputStream, System.out)
                    inheritProcessIO(xrayProc.errorStream, System.err)
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

                    val passwordResult = tunProc.handleSudoPassword(password)
                    if (!passwordResult) {
                        throw WrongSudoPasswordException()
                    }

                    inheritProcessIO(tunProc.inputStream, System.out)
                    inheritProcessIO(tunProc.errorStream, System.err)

                    tunProc.addShutdownHook()

                    runningProcMap[ProcType.TUN] = tunProc

                    // Notify UI
                    JvmV2RayInteractor.postConnected()
                } catch (exception: Throwable) {
                    exception.printStackTrace()

                    stopService()
                    JvmV2RayInteractor.postDisconnected()

                    if (exception is WrongSudoPasswordException) {
                        SystemInteractor.clearSudoPassword()
                        JvmV2RayInteractor.postWrongSudoPassword()
                    }
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

    override fun stopService() {
        runningProcMap[ProcType.TUN]?.close()
        runningProcMap[ProcType.XRAY]?.close()

        runningProcMap.clear()
        JvmV2RayInteractor.postDisconnected()
    }
}
