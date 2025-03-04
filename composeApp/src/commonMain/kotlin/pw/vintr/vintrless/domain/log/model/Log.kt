package pw.vintr.vintrless.domain.log.model

import kotlinx.datetime.Clock

data class Log(
    val payload: String,
    val type: LogType,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)
