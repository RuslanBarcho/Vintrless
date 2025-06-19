package pw.vintr.vintrless.tools.extensions

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import pw.vintr.vintrless.domain.log.JVMLogInteractor
import pw.vintr.vintrless.domain.log.model.LogType
import java.io.IOException
import java.io.InputStream
import java.io.PrintStream
import java.lang.Runnable
import java.nio.charset.StandardCharsets
import java.util.*

fun Process.close() {
    descendants()?.forEach(ProcessHandle::destroy)
    destroy()
    inputStream?.close()
    outputStream?.close()
}

fun Process.addShutdownHook() {
    val shutdownRunnable = Runnable { close() }
    Runtime.getRuntime().addShutdownHook(Thread(shutdownRunnable))
}

fun Process?.closeIfAlive() {
    if (this?.isAlive == true) {
        this.close()
    }
}

fun inheritProcessIO(src: InputStream, dest: PrintStream) {
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

suspend fun Process.handleSudoPassword(password: String): Boolean {
    // Create a channel to detect errors
    val errorChannel = Channel<Boolean>(1)

    // Write password to sudo
    this.outputStream.bufferedWriter().use {
        it.write("$password\n")
        it.flush()
    }

    // Launch error monitoring in background
    CoroutineScope(Dispatchers.IO).launch {
        val reader = errorStream.bufferedReader()
        try {
            val line = reader.readLine()

            if (
                line?.contains("Password:Sorry, try again") == true ||
                line?.contains("incorrect password attempt") == true ||
                line?.contains("a password is required") == true
            ) {
                errorChannel.send(true)
                reader.close()
            } else {
                errorChannel.send(false)
            }
        } catch (e: IOException) {
            // Stream closed normally
        }
    }

    return try {
        withTimeoutOrNull(5000) {
            !errorChannel.receive()
        } ?: true
    } finally {
        errorChannel.close()
    }
}
