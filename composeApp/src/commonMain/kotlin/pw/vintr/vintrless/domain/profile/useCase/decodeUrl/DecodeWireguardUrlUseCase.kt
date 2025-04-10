package pw.vintr.vintrless.domain.profile.useCase.decodeUrl

import com.eygraber.uri.Url
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType
import pw.vintr.vintrless.tools.extensions.getQueryParams

object DecodeWireguardUrlUseCase {

    operator fun invoke(urlString: String): ProfileData {
        val url = Url.parse(urlString)
        val profileDataMap = ProfileForm.Vless
            .getDefaultData()
            .toMutableMap()

        val queryParams = url.getQueryParams()

        profileDataMap[ProfileField.Name.key] = url.fragment ?: "Profile"
        profileDataMap[ProfileField.IP.key] = url.host
        profileDataMap[ProfileField.Port.key] = url.port.toString()
        profileDataMap[ProfileField.ServerPrivateKey.key] = url.userInfo.orEmpty()
        profileDataMap[ProfileField.ServerPublicKey.key] = queryParams[ProfileField.ServerPublicKey.queryKey].orEmpty()
        profileDataMap[ProfileField.ServerAdditionalKey.key] = queryParams[ProfileField.ServerAdditionalKey.queryKey].orEmpty()
        profileDataMap[ProfileField.Reserved.key] = queryParams[ProfileField.Reserved.queryKey].orEmpty()
        profileDataMap[ProfileField.LocalAddress.key] = queryParams[ProfileField.LocalAddress.queryKey].orEmpty()
        profileDataMap[ProfileField.MTU.key] = queryParams[ProfileField.MTU.queryKey].orEmpty()

        return ProfileData(
            type = ProtocolType.WIREGUARD,
            data = profileDataMap,
        )
    }
}
