package pw.vintr.vintrless.log

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pw.vintr.vintrless.domain.log.interactor.LogPlatformInteractor
import pw.vintr.vintrless.domain.log.model.Log
import pw.vintr.vintrless.domain.log.model.LogType
import pw.vintr.vintrless.domain.log.model.LogsContainer
import pw.vintr.vintrless.tools.coroutines.createExceptionHandler
import pw.vintr.vintrless.tools.extensions.cancelIfActive
import pw.vintr.vintrless.tools.list.boundedListOf

class AndroidLogInteractor : LogPlatformInteractor() {

    companion object {
        private const val MAX_LOG_BUFFER = 5000
    }

    private val _logFlow = MutableStateFlow(LogsContainer(logs = boundedListOf(MAX_LOG_BUFFER)))

    override val logFlow = _logFlow
        .shareIn(this, started = SharingStarted.Eagerly, replay = 1)

    private var loggingProc: Process? = null

    private var loggingJob: Job? = null

    init {
        startInheritLogs()
    }

    private fun startInheritLogs() {
        loggingJob.cancelIfActive()
        loggingJob = launch(createExceptionHandler()) {
            loggingProc = withContext(Dispatchers.IO) {
                Runtime
                    .getRuntime()
                    .exec(arrayOf(
                        "logcat",
                        "-v",
                        "time",
                        "-s",
                        "GoLog,tun2socks,AndroidRuntime,System.err"
                    ))
            }

            withContext(Dispatchers.IO) {
                loggingProc
                    ?.inputStream
                    ?.bufferedReader()
                    ?.useLines { lines ->
                        lines.forEach { line ->
                            appendLog(line)
                        }
                    }
            }
        }
    }

    private fun stopInheritLogs() {
        loggingProc?.destroy()
        loggingJob.cancelIfActive()
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
}
