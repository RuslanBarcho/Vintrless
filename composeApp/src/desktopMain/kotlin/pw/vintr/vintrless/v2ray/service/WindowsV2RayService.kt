package pw.vintr.vintrless.v2ray.service

import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.tools.PathProvider
import pw.vintr.vintrless.tools.extensions.addShutdownHook
import pw.vintr.vintrless.tools.extensions.close
import pw.vintr.vintrless.v2ray.interactor.JvmV2RayInteractor
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.util.*

object WindowsV2RayService {

    enum class ProcType {
        TUN,
        XRAY
    }

    private const val PROCESS_START_DELAY_MILLIS = 300L

    private val runningProcMap: MutableMap<ProcType, Process> = mutableMapOf()

    private val resourcesDir = File(PathProvider.resourcesPath)

    private var serviceStartJob: Job? = null

    fun startService(config: V2RayEncodedConfig) {
        serviceStartJob = MainScope().launch {
            JvmV2RayInteractor.postConnecting()

            try {
                // Save config to file
                saveXrayConfig(config)

                // Start xray listener
                val xrayProc = ProcessBuilder("cmd", "/c", "xray", "run", "-c", "config_xray.json")
                    .directory(resourcesDir)
                    .start()

                inheritIO(xrayProc.inputStream, System.out)
                inheritIO(xrayProc.errorStream, System.err)
                xrayProc.addShutdownHook()

                runningProcMap[ProcType.XRAY] = xrayProc

                delay(PROCESS_START_DELAY_MILLIS)

                // Start TUN
                val tunProc = ProcessBuilder("cmd", "/c", "sing-box", "run", "-c", "config_singbox.json")
                    .directory(resourcesDir)
                    .start()

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

    private fun saveXrayConfig(config: V2RayEncodedConfig) {
        val xrayConfigFile = File("${PathProvider.resourcesPath}\\config_xray.json")

        // Remove current config if exist
        if (xrayConfigFile.exists()) {
            xrayConfigFile.delete()
            xrayConfigFile.createNewFile()
        }

        // Write new config content
        val fileWriter = FileWriter(xrayConfigFile.absoluteFile)
        val bufferedWriter = BufferedWriter(fileWriter)

        bufferedWriter.write(config.configJson)
        bufferedWriter.close()
    }

    private fun inheritIO(src: InputStream, dest: PrintStream) {
        Thread {
            val sc = Scanner(src)
            while (sc.hasNextLine()) {
                dest.println(String(sc.nextLine().toByteArray(), StandardCharsets.UTF_8))
            }
        }.start()
    }

    fun stopService() {
        runningProcMap[ProcType.TUN]?.close()
        runningProcMap[ProcType.XRAY]?.close()

        runningProcMap.clear()
        JvmV2RayInteractor.postDisconnected()
    }
}
