package pw.vintr.vintrless.domain.profile.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProfileType(val profileName: String) {
    VLESS(
        profileName = "VLESS"
    );
}
