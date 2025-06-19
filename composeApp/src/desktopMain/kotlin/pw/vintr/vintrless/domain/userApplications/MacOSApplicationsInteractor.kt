package pw.vintr.vintrless.domain.userApplications

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skiko.toBitmap
import pw.vintr.vintrless.domain.system.interactor.SystemInteractor
import pw.vintr.vintrless.domain.system.model.SudoPasswordRequestReason
import pw.vintr.vintrless.domain.userApplications.model.common.application.UserApplication
import pw.vintr.vintrless.domain.userApplications.model.common.application.UserApplicationPayload
import pw.vintr.vintrless.domain.userApplications.model.common.process.SystemProcess
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.imageio.ImageIO

class MacOSApplicationsInteractor : ApplicationsInteractor() {

    private suspend fun executeWithSudo(
        command: String,
        password: String
    ): String = withContext(Dispatchers.IO) {
        val processBuilder = ProcessBuilder(
            "/bin/bash", "-c", "echo '$password' | sudo -S $command"
        )

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val errorReader = BufferedReader(InputStreamReader(process.errorStream))

        val output = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            output.appendLine(line)
        }

        val errorOutput = StringBuilder()
        while (errorReader.readLine().also { line = it } != null) {
            errorOutput.appendLine(line)
        }

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw RuntimeException("Command failed with exit code $exitCode: $errorOutput")
        }

        output.toString()
    }

    override suspend fun getApplications(): List<UserApplication> = withContext(Dispatchers.IO) {
        val applications = mutableListOf<UserApplication>()
        val password = SystemInteractor.getSudoPassword(SudoPasswordRequestReason.GET_APPLICATIONS_INFO)

        // System applications (might need sudo for some locations)
        if (password != null) {
            try {
                // Use sudo to access protected directories
                val systemAppsOutput = executeWithSudo(
                    "find /Applications -name '*.app' -maxdepth 3",
                    password
                )
                parseAppFinderOutput(systemAppsOutput, applications)
            } catch (e: Exception) {
                // Fall back to regular scanning if sudo fails
                SystemInteractor.clearSudoPassword()
                scanApplicationsInDirectory("/Applications", applications)
            }
        } else {
            scanApplicationsInDirectory("/Applications", applications)
        }

        // User applications (don't need sudo)
        val userHome = System.getProperty("user.home")
        scanApplicationsInDirectory("$userHome/Applications", applications)

        applications
    }

    private fun parseAppFinderOutput(
        output: String,
        applications: MutableList<UserApplication>
    ) {
        output.lines().forEach { line ->
            if (line.isNotBlank()) {
                val appFile = File(line.trim())
                if (appFile.exists() && appFile.isDirectory) {
                    val appName = appFile.name.removeSuffix(".app")
                    val executableName = getExecutableNameFromAppBundle(appFile)

                    applications.add(
                        UserApplication(
                            name = appName,
                            payload = UserApplicationPayload.MacOSApplicationPayload(
                                processName = executableName ?: appName,
                                absolutePath = appFile.absolutePath
                            )
                        )
                    )
                }
            }
        }
    }

    private fun scanApplicationsInDirectory(
        directory: String,
        applications: MutableList<UserApplication>
    ) {
        val dir = File(directory)
        if (!dir.exists() || !dir.isDirectory) return

        dir.listFiles()?.forEach { file ->
            if (file.isDirectory && file.name.endsWith(".app")) {
                val appName = file.name.removeSuffix(".app")
                val executableName = getExecutableNameFromAppBundle(file)

                applications.add(
                    UserApplication(
                        name = appName,
                        payload = UserApplicationPayload.MacOSApplicationPayload(
                            processName = executableName ?: appName,
                            absolutePath = file.absolutePath
                        )
                    )
                )
            }
        }
    }

    private fun getExecutableNameFromAppBundle(appBundle: File): String? {
        val plistFile = File("${appBundle.absolutePath}/Contents/Info.plist")
        if (!plistFile.exists()) return null

        // Simple parsing of Info.plist to get CFBundleExecutable
        try {
            val content = plistFile.readText()
            val regex = Regex("<key>CFBundleExecutable</key>\\s*<string>(.*?)</string>")
            return regex.find(content)?.groupValues?.get(1)
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun getRunningProcesses(): List<SystemProcess> = withContext(Dispatchers.IO) {
        val password = SystemInteractor.getSudoPassword(SudoPasswordRequestReason.GET_APPLICATIONS_INFO)

        return@withContext if (password != null) {
            // Use ps with sudo for more detailed process info
            try {
                val psOutput = executeWithSudo(
                    "ps -eo pid,user,comm,command | awk 'NR>1'",
                    password
                )
                parsePsOutput(psOutput)
            } catch (e: Exception) {
                SystemInteractor.clearSudoPassword()
                listOf()
            }
        } else {
            listOf()
        }
    }

    private fun parsePsOutput(output: String): List<SystemProcess> {
        return output.lines()
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val parts = line.trim().split("\\s+".toRegex(), limit = 4)
                if (parts.size >= 4) {
                    val command = parts[2]
                    val fullCommand = parts[3]

                    // Try to extract a more readable name
                    val appName = if (
                        fullCommand.contains("/Applications/") &&
                        fullCommand.contains(".app/")
                    ) {
                        fullCommand.substringAfter("/Applications/")
                            .substringBefore(".app/")
                            .substringAfterLast("/") + ".app"
                    } else {
                        command
                    }

                    SystemProcess(
                        appName = appName,
                        processName = command
                    )
                } else {
                    null
                }
            }
    }

    override suspend fun getApplicationIcon(application: UserApplication): ImageBitmap? {
        return when (application.payload) {
            is UserApplicationPayload.MacOSApplicationPayload -> {
                getExecutableBufferedImage(File(application.payload.absolutePath))
                    ?.toBitmap()
                    ?.asComposeImageBitmap()
            }
            else -> null
        }
    }

    private suspend fun getExecutableBufferedImage(
        executable: File
    ): BufferedImage? = withContext(Dispatchers.IO) {
        runCatching {
            // Validate this is a .app bundle
            if (!executable.isDirectory || !executable.name.endsWith(".app")) {
                return@runCatching null
            }

            // Try to find the icon file
            val appName = executable.name.removeSuffix(".app")
            val possibleIconPaths = listOf(
                "${executable.absolutePath}/Contents/Resources/$appName.icns",
                "${executable.absolutePath}/Contents/Resources/AppIcon.icns",
                "${executable.absolutePath}/Contents/Resources/application.icns"
            )

            val iconFile = possibleIconPaths.firstOrNull { File(it).exists() }?.let { File(it) }
                ?: return@runCatching null

            // Approach 1: Try using macOS's sips command (most reliable)
            try {
                return@runCatching convertWithSips(iconFile)
            } catch (e: Exception) {
                println("sips conversion failed: ${e.message}")
            }

            // Approach 2: Try reading as PNG directly (some .icns contain PNGs)
            try {
                return@runCatching readPngFromIcns(iconFile)
            } catch (e: Exception) {
                println("Direct PNG read failed: ${e.message}")
            }

            null
        }.getOrNull()
    }

    private fun convertWithSips(icnsFile: File): BufferedImage? {
        val tempFile = File.createTempFile("icon", ".png")
        tempFile.deleteOnExit()

        val process = Runtime.getRuntime().exec(arrayOf(
            "sips", "-s", "format", "png",
            icnsFile.absolutePath,
            "--out", tempFile.absolutePath
        ))
        process.waitFor()

        if (process.exitValue() != 0) {
            throw RuntimeException("sips conversion failed")
        }

        return ImageIO.read(tempFile)?.let { image ->
            // Scale down if too large
            if (image.width > 512 || image.height > 512) {
                val scaled = image.getScaledInstance(256, 256, Image.SCALE_SMOOTH)
                val buffered = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
                buffered.graphics.drawImage(scaled, 0, 0, null)
                buffered
            } else {
                image
            }
        }
    }

    private fun readPngFromIcns(icnsFile: File): BufferedImage? {
        // Read the file as binary data
        val bytes = icnsFile.readBytes()

        // PNG magic number (89 50 4E 47 0D 0A 1A 0A)
        val pngMagic = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)

        // Find PNG header position
        val pngStart = bytes.indexOfSequence(pngMagic)
        if (pngStart == -1) return null

        // Extract PNG data
        val pngBytes = bytes.copyOfRange(pngStart, bytes.size)
        return ImageIO.read(pngBytes.inputStream())
    }

    // Replacement for indexOfSlice
    private fun ByteArray.indexOfSequence(sequence: ByteArray): Int {
        outer@ for (i in 0..this.size - sequence.size) {
            for (j in sequence.indices) {
                if (this[i + j] != sequence[j]) {
                    continue@outer
                }
            }
            return i
        }
        return -1
    }
}
