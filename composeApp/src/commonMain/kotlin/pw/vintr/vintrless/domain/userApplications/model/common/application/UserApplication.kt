package pw.vintr.vintrless.domain.userApplications.model.common.application

import pw.vintr.vintrless.domain.userApplications.model.common.IDeviceApplication
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class UserApplication(
    val name: String,
    val payload: UserApplicationPayload,
) : IDeviceApplication {

    @OptIn(ExperimentalUuidApi::class)
    val lazyListUUID = Uuid.random().toString()

    override val uuid: String = lazyListUUID

    override val appName: String = name

    override val processName: String = payload.payloadReadableTitle

    override val savedByUser: Boolean = false
}
