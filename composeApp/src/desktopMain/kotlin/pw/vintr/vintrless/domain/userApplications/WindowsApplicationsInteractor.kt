package pw.vintr.vintrless.domain.userApplications

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pw.vintr.vintrless.domain.userApplications.model.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
import pw.vintr.vintrless.domain.userApplications.model.UserApplicationPayload
import pw.vintr.vintrless.tools.PathProvider
import pw.vintr.vintrless.tools.extensions.Empty
import pw.vintr.vintrless.tools.extensions.addShutdownHook
import pw.vintr.vintrless.tools.extensions.closeIfAlive
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.coroutines.suspendCoroutine

class WindowsApplicationsInteractor : ApplicationsInteractor() {

    companion object {
        private const val EXE_EXTENSION = ".exe"
        private const val PROC_START_ARGUMENT = "--processStart"
    }

    @Serializable
    private data class StartMenuRecord(
        @SerialName("name")
        val name: String,
        @SerialName("location")
        val location: String,
        @SerialName("arguments")
        val arguments: String,
        @SerialName("workingDirectory")
        val workingDirectory: String,
    )

    private val resourcesDir = File(PathProvider.resourcesPath)

    private var getApplicationsProcess: Process? = null

    private var getRunningProcessesProcess: Process? = null

    override suspend fun getApplications(): List<UserApplication> {
        return mapStartRecordsToApplications(getStartMenuRecords())
    }

    override suspend fun getRunningProcesses(): List<SystemProcess> {
        return withContext(Dispatchers.IO) {
            getRunningProcessesProcess.closeIfAlive()

            val proc = ProcessBuilder("powershell.exe", ".\\find_processes.ps1")
                .directory(resourcesDir)
                .start()
                .apply { addShutdownHook() }

            getRunningProcessesProcess = proc
            inheritErrorIO(proc.errorStream)

            val processList = mutableListOf<String>()
            val sc = Scanner(proc.inputStream, StandardCharsets.UTF_8)

            while (sc.hasNextLine() && proc.isAlive) {
                val bytearray = sc.nextLine().toByteArray(StandardCharsets.UTF_8)
                val outputLine = String(bytearray, StandardCharsets.UTF_8)

                processList.add(outputLine)
            }

            processList
                .distinct()
                .sorted()
                .map { processName ->
                    SystemProcess(
                        appName = processName,
                        processName = processName + EXE_EXTENSION
                    )
                }
        }
    }

    private suspend fun getStartMenuRecords(): List<StartMenuRecord> = suspendCoroutine { continuation ->
        getApplicationsProcess.closeIfAlive()

        val proc = ProcessBuilder("powershell.exe", ".\\find_applications.ps1")
            .directory(resourcesDir)
            .start()
            .apply { addShutdownHook() }

        getApplicationsProcess = proc
        inheritErrorIO(proc.errorStream)

        Thread {
            val appList = mutableListOf<StartMenuRecord>()
            val sc = Scanner(proc.inputStream, StandardCharsets.UTF_8)

            while (sc.hasNextLine() && proc.isAlive) {
                val bytearray = sc.nextLine().toByteArray(StandardCharsets.UTF_8)
                val outputLine = String(bytearray, StandardCharsets.UTF_8)

                runCatching {
                    appList.add(Json.decodeFromString(outputLine))
                }.onFailure { it.printStackTrace() }
            }

            continuation.resumeWith(Result.success(appList))
        }.start()
    }

    private fun mapStartRecordsToApplications(
        records: List<StartMenuRecord>
    ): List<UserApplication> {
        return records.map { record ->
            val executableName: String
            val executablePath: String

            if (record.arguments.contains(PROC_START_ARGUMENT)) {
                executableName = record.arguments
                    .replace(PROC_START_ARGUMENT, String.Empty)
                    .replace("\"", String.Empty)
                    .trim()
                executablePath = "${record.workingDirectory}\\$executableName"
            } else {
                executableName = record.location.split("\\").last()
                executablePath = record.location
            }

            UserApplication(
                name = record.name,
                payload = UserApplicationPayload.WindowsApplicationPayload(
                    relatedExecutables = listOf(
                        UserApplicationPayload.WindowsApplicationPayload.Executable(
                            processName = executableName,
                            absolutePath = executablePath,
                        )
                    )
                )
            )
        }.sortedBy { it.name }
    }

    private fun inheritErrorIO(src: InputStream) {
        Thread {
            val sc = Scanner(src)
            while (sc.hasNextLine()) {
                System.err.println(String(sc.nextLine().toByteArray(), StandardCharsets.UTF_8))
            }
        }.start()
    }
}
