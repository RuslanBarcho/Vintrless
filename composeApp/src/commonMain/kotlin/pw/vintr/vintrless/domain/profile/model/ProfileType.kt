package pw.vintr.vintrless.domain.profile.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProfileType(
    val protocolName: String,
    val code: String,
) {
    VLESS(
        protocolName = "VLESS",
        code = "vless"
    );

    companion object {
        fun getByCode(code: String) = ProfileType.entries.find { it.code == code } ?: ProfileType.entries.first()
    }
}
