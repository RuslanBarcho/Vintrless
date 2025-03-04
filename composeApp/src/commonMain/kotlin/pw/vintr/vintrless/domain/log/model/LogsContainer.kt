package pw.vintr.vintrless.domain.log.model

import pw.vintr.vintrless.tools.list.BoundedList

data class LogsContainer(
    val logs: BoundedList<Log>
) {
    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return logs.hashCode()
    }
}
