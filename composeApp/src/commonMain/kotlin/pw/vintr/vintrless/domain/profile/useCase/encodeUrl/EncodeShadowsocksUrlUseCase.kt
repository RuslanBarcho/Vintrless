package pw.vintr.vintrless.domain.profile.useCase.encodeUrl

import com.eygraber.uri.UriCodec
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.profile.useCase.encodeUrl.base.BaseEncodeProfileUrlUseCase
import pw.vintr.vintrless.tools.network.IPTools
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object EncodeShadowsocksUrlUseCase : BaseEncodeProfileUrlUseCase() {

    @OptIn(ExperimentalEncodingApi::class)
    override fun invoke(profile: ProfileData): String {
        val form = ProfileForm.getByType(profile.type)

        // Build authority info
        val security = profile.data[ProfileField.SSocksSecurity.key]
        val password = profile.data[ProfileField.Password.key]

        val authInfo = "${security}:${password}"

        // Build url
        val ip = IPTools.getIpv6Address(profile.ip)
        val port = profile.data[ProfileField.Port.key].orEmpty()

        val url = "${Base64.encode(authInfo.encodeToByteArray())}@$ip:$port"

        // Build name
        val name = UriCodec.encode(profile.name)

        return "${form.type.protocolScheme}$url#$name"
    }
}
