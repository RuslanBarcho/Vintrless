package pw.vintr.vintrless.domain.userApplications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
import pw.vintr.vintrless.domain.userApplications.model.UserApplicationPayload
import pw.vintr.vintrless.tools.PathProvider
import pw.vintr.vintrless.tools.extensions.Empty
import pw.vintr.vintrless.tools.extensions.addShutdownHook
import pw.vintr.vintrless.tools.extensions.close
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.coroutines.suspendCoroutine

class WindowsApplicationsInteractor : ApplicationsInteractor() {

    companion object {
        private const val FIND_COMMAND = "\$OutputEncoding = [console]::InputEncoding = " +
                "[console]::OutputEncoding = New-Object System.Text.UTF8Encoding; " +
                "foreach(\$UKey in 'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\*" +
                "','HKLM:\\SOFTWARE\\Wow6432node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\*" +
                "','HKCU:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\*" +
                "','HKCU:\\SOFTWARE\\Wow6432node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\*')" +
                "{foreach (\$Product in (Get-ItemProperty \$UKey -ErrorAction SilentlyContinue)){" +
                "if(\$Product.DisplayName -and \$Product.SystemComponent -ne 1){" +
                "\$Product.DisplayName + '|' + \$Product.InstallLocation}}}"

        private const val EXE_EXTENSION = ".exe"

        private val invalidExeNames = listOf("unins000", "install", "uninstall", "update")
    }

    @Serializable
    private data class HLKRecord(
        @SerialName("name")
        val name: String,
        @SerialName("location")
        val location: String,
    )

    private val resourcesDir = File(PathProvider.resourcesPath)

    private var findProcess: Process? = null

    override suspend fun getApplications(): List<UserApplication> {
        return mapHLKToApplications(getHLKRecords())
    }

    private suspend fun getHLKRecords(): List<HLKRecord> = suspendCoroutine { continuation ->
        if (findProcess != null && findProcess?.isAlive == true) {
            findProcess?.close()
            findProcess = null
        }

        val proc = ProcessBuilder("powershell.exe", FIND_COMMAND)
            .directory(resourcesDir)
            .start()
            .apply { addShutdownHook() }

        findProcess = proc

        inheritErrorIO(proc.errorStream)

        Thread {
            val appList = mutableListOf<HLKRecord>()
            val sc = Scanner(proc.inputStream, StandardCharsets.UTF_8)

            while (sc.hasNextLine() && proc.isAlive) {
                val bytearray = sc.nextLine().toByteArray(StandardCharsets.UTF_8)
                val outputLine = String(bytearray, StandardCharsets.UTF_8)

                val outputData = outputLine.split("|")

                val name = outputData.getOrNull(0)
                val location = outputData.getOrNull(1)

                if (name != null && location != null) {
                    appList.add(
                        HLKRecord(
                            name = name,
                            location = location
                        )
                    )
                }
            }

            continuation.resumeWith(Result.success(appList))
        }.start()
    }

    private suspend fun mapHLKToApplications(
        records: List<HLKRecord>
    ): List<UserApplication> = suspendCoroutine { continuation ->
        Thread {
            val applications = records.filter {
                it.location.isNotEmpty()
            }.mapNotNull { installedApp ->
                val folder = File(installedApp.location)
                val executables = folder.listFiles()
                    ?.toList()
                    ?.getExecutables(installedApp.name)
                    .orEmpty()

                if (executables.isNotEmpty()) {
                    UserApplication(
                        name = installedApp.name,
                        payload = UserApplicationPayload.WindowsApplicationPayload(
                            relatedExecutables = executables.map { executable ->
                                UserApplicationPayload.WindowsApplicationPayload.Executable(
                                    processName = executable.name,
                                    absolutePath = executable.absolutePath,
                                )
                            }
                        )
                    )
                } else {
                    null
                }
            }

            continuation.resumeWith(Result.success(applications.sortedBy { it.name }))
        }.start()
    }

    private fun List<File>.getExecutables(applicationName: String): List<File> {
        val executables = mutableListOf<File>()

        forEach { entry ->
            if (entry.isFile) {
                if (
                    entry.name.endsWith(EXE_EXTENSION) &&
                    entry.name
                        .contains(applicationName, ignoreCase = true)
                ) {
                    return listOf(entry)
                }

                if (
                    entry.name.endsWith(EXE_EXTENSION) &&
                    !invalidExeNames.any {
                        entry.name
                            .replace(EXE_EXTENSION, String.Empty)
                            .equals(it, ignoreCase = true)
                    }
                ) {
                    executables.add(entry)
                }
            } else if (entry.isDirectory) {
                entry.listFiles()?.toList()
                    ?.getExecutables(applicationName)
                    ?.let { executables.addAll(it) }
            }
        }

        return executables
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
