package pw.vintr.vintrless.domain.userApplications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
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
                val executable = folder.listFiles()
                    ?.toList()
                    ?.getMainExecutable()

                if (executable != null) {
                    UserApplication(
                        name = installedApp.name,
                        processName = executable.name,
                        executablePath = executable.absolutePath,
                    )
                } else {
                    null
                }
            }

            continuation.resumeWith(Result.success(applications.sortedBy { it.name }))
        }.start()
    }

    private fun List<File>.getMainExecutable(): File? {
        val directories = this.filter { it.isDirectory }

        for (file in this.filter { it.isFile }) {
            if (
                file.name.endsWith(EXE_EXTENSION) &&
                !invalidExeNames.any {
                    file.name
                        .replace(EXE_EXTENSION, String.Empty)
                        .equals(it, ignoreCase = true)
                }
            ) {
                return file
            }
        }

        for (directory in directories) {
            directory.listFiles()?.toList()?.getMainExecutable()?.let { return it }
        }

        return null
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
