package pw.vintr.vintrless.domain.profile.useCase.encodeUrl

import com.eygraber.uri.UriCodec
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.profile.useCase.encodeUrl.base.BaseEncodeProfileUrlUseCase
import pw.vintr.vintrless.tools.network.IPTools

object EncodeHttpUrlUseCase : BaseEncodeProfileUrlUseCase() {

    override fun invoke(profile: ProfileData): String {
        val form = ProfileForm.getByType(profile.type)

        // Build authority info
        val username = profile.data[ProfileField.UserName.key]
        val password = profile.data[ProfileField.Password.key]

        val authInfo = "${username}:${password}"

        // Build url
        val ip = IPTools.getIpv6Address(profile.ip)
        val port = profile.data[ProfileField.Port.key].orEmpty()

        val url = "${UriCodec.encode(authInfo)}@$ip:$port"

        // Build name
        val name = UriCodec.encode(profile.name)

        return "${form.type.protocolScheme}$url#$name"
    }
}
