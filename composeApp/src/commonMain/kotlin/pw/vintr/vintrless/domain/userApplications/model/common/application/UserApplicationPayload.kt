package pw.vintr.vintrless.domain.userApplications.model.common.application

sealed class UserApplicationPayload {

    abstract val payloadReadableTitle: String

    data class AndroidApplicationPayload(
        val packageName: String,
    ) : UserApplicationPayload() {

        override val payloadReadableTitle: String = packageName
    }

    data class WindowsApplicationPayload(
        val relatedExecutable: Executable,
    ) : UserApplicationPayload() {

        data class Executable(
            val processName: String,
            val absolutePath: String,
        )

        override val payloadReadableTitle: String = relatedExecutable
            .processName
    }

    data class MacOSApplicationPayload(
        val processName: String,
        val absolutePath: String
    ) : UserApplicationPayload() {

        override val payloadReadableTitle: String = processName
    }
}
