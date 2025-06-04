package pw.vintr.vintrless.tools.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class BashCommandExecutor {
    suspend fun executeWithSudo(command: String, password: String): String = withContext(Dispatchers.IO) {
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

    suspend fun execute(command: String): String = withContext(Dispatchers.IO) {
        val process = Runtime.getRuntime().exec(arrayOf("/bin/bash", "-c", command))
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        val output = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            output.appendLine(line)
        }

        process.waitFor()
        output.toString()
    }
}
