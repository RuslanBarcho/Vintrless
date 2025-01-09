package pw.vintr.vintrless.domain.userApplications.model

import pw.vintr.vintrless.tools.extensions.Comma
import pw.vintr.vintrless.tools.extensions.Space

sealed class UserApplicationPayload {

    abstract val payloadReadableTitle: String

    data class AndroidApplicationPayload(
        val packageName: String,
    ) : UserApplicationPayload() {

        override val payloadReadableTitle: String = packageName
    }

    data class WindowsApplicationPayload(
        val relatedExecutables: List<Executable>,
    ) : UserApplicationPayload() {

        data class Executable(
            val processName: String,
            val absolutePath: String,
        )

        override val payloadReadableTitle: String = relatedExecutables
            .map { it.processName }
            .distinct()
            .joinToString(String.Comma + String.Space)
    }
}
