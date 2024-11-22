package pw.vintr.vintrless.domain.profile.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProfileType(
    val profileName: String,
    val code: String,
) {
    VLESS(
        profileName = "VLESS",
        code = "vless"
    );

    companion object {
        fun getByCode(code: String) = ProfileType.entries.find { it.code == code } ?: ProfileType.entries.first()
    }
}
