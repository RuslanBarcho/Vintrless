package pw.vintr.vintrless.log

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import pw.vintr.vintrless.domain.log.interactor.LogPlatformInteractor
import pw.vintr.vintrless.domain.log.model.Log
import pw.vintr.vintrless.domain.log.model.LogType
import pw.vintr.vintrless.domain.log.model.LogsContainer
import pw.vintr.vintrless.tools.coroutines.createExceptionHandler
import pw.vintr.vintrless.tools.extensions.cancelIfActive
import pw.vintr.vintrless.tools.list.boundedListOf
import java.io.BufferedReader
import java.io.InterruptedIOException

class AndroidLogInteractor : LogPlatformInteractor() {

    companion object {
        private const val MAX_LOG_BUFFER = 5000
    }

    private val _logFlow = MutableStateFlow(LogsContainer(logs = boundedListOf(MAX_LOG_BUFFER)))

    override val logFlow = _logFlow
        .shareIn(this, started = SharingStarted.Lazily, replay = 1)

    override val isLoggingActive: Boolean
        get() = loggingProc != null || loggingJob?.isActive == true

    private var loggingProc: Process? = null

    private var loggingJob: Job? = null

    private var bufferReader: BufferedReader? = null

    override fun startInheritLogs() {
        // Stop previous
        if (isLoggingActive) {
            stopInheritLogs()
        }

        // Clear recent buffer
        _logFlow.value = LogsContainer(logs = boundedListOf(MAX_LOG_BUFFER))

        // Start new
        loggingJob = launch(Dispatchers.IO + createExceptionHandler()) {
            try {
                loggingProc = Runtime
                    .getRuntime()
                    .exec(arrayOf(
                        "logcat",
                        "-v",
                        "time",
                        "-s",
                        "GoLog,tun2socks,AndroidRuntime,System.err"
                    ))

                bufferReader = loggingProc
                    ?.inputStream
                    ?.bufferedReader()

                bufferReader
                    ?.useLines { lines ->
                        lines.forEach { line ->
                            runCatching { appendLog(line) }
                        }
                    }
            } catch (_: InterruptedIOException) {}
        }
    }

    override fun stopInheritLogs() {
        loggingJob.cancelIfActive()
        loggingProc?.destroy()
        bufferReader?.close()

        loggingProc = null
        loggingJob = null
        bufferReader = null
    }

    private fun appendLog(logText: String) {
        _logFlow.value = _logFlow.value.copy(
            logs = _logFlow.value.logs.apply {
                add(
                    Log(
                        payload = logText,
                        type = when {
                            logText.contains("E/") -> LogType.ERROR
                            logText.contains("err", ignoreCase = true) -> LogType.ERROR
                            logText.contains("warning", ignoreCase = true) -> LogType.WARNING
                            else -> LogType.INFORMATION
                        }
                    )
                )
            }
        )
    }

    override suspend fun clearLogs() {
        // Stop runtime logs
        stopInheritLogs()

        // Clear logs from logcat and buffer
        withContext(Dispatchers.IO) {
            Runtime
                .getRuntime()
                .exec(arrayOf("logcat", "-c"))
                .waitFor()
        }
        _logFlow.value = LogsContainer(logs = boundedListOf(MAX_LOG_BUFFER))

        // Start runtime logs
        startInheritLogs()
    }

    override fun close() {
        stopInheritLogs()
    }
}
