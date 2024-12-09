package pw.vintr.vintrless.v2ray.service

import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
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

    private val runningProcMap: MutableMap<ProcType, Process> = mutableMapOf()

    private val resourcesDir = File(System.getProperty("compose.application.resources.dir"))

    fun startService(config: V2RayEncodedConfig) {
        JvmV2RayInteractor.postConnecting()

        try {
            saveXrayConfig(config)

            val tunProc = ProcessBuilder("cmd", "/c", "sing-box", "run", "-c", "config_singbox.json")
                .directory(resourcesDir)
                .start()

            inheritIO(tunProc.inputStream, System.out)
            inheritIO(tunProc.errorStream, System.err)
            tunProc.addShutdownHook()

            runningProcMap[ProcType.TUN] = tunProc

            val xrayProc = ProcessBuilder("cmd", "/c", "xray", "run", "-c", "config_xray.json")
                .directory(resourcesDir)
                .start()

            inheritIO(xrayProc.inputStream, System.out)
            inheritIO(xrayProc.errorStream, System.err)
            xrayProc.addShutdownHook()

            runningProcMap[ProcType.XRAY] = xrayProc

            JvmV2RayInteractor.postConnected()
        } catch (exception: Throwable) {
            exception.printStackTrace()

            stopService()
            JvmV2RayInteractor.postDisconnected()
        }
    }

    private fun saveXrayConfig(config: V2RayEncodedConfig) {
        val xrayConfigFile = File("${resourcesDir.absolutePath}\\config_xray.json")

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

    private fun Process.close() {
        descendants()?.forEach(ProcessHandle::destroy)
        destroy()
        inputStream?.close()
        outputStream?.close()
    }

    private fun Process.addShutdownHook() {
        val shutdownRunnable = Runnable { close() }
        Runtime.getRuntime().addShutdownHook(Thread(shutdownRunnable))
    }
}
