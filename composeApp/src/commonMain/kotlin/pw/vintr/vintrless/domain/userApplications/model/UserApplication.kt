package pw.vintr.vintrless.domain.userApplications.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class UserApplication(
    val name: String,
    val payload: UserApplicationPayload,
) {

    @OptIn(ExperimentalUuidApi::class)
    val lazyListUUID = Uuid.random().toString()
}
