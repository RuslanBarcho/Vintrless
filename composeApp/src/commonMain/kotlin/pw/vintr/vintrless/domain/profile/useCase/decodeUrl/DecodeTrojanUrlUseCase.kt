package pw.vintr.vintrless.domain.profile.useCase.decodeUrl

import com.eygraber.uri.Url
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.profile.useCase.decodeUrl.network.DecodeNetworkUrlUseCase
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType
import pw.vintr.vintrless.tools.extensions.getQueryParams

object DecodeTrojanUrlUseCase {

    operator fun invoke(urlString: String): ProfileData {
        val url = Url.parse(urlString)
        val profileDataMap = ProfileForm.Trojan
            .getDefaultData()
            .toMutableMap()

        val queryParams = url.getQueryParams()

        // Base data
        profileDataMap[ProfileField.Name.key] = url.fragment ?: "Profile"
        profileDataMap[ProfileField.IP.key] = url.host
        profileDataMap[ProfileField.Port.key] = url.port.toString()
        profileDataMap[ProfileField.Password.key] = url.userInfo.orEmpty()

        // Network
        profileDataMap.putAll(DecodeNetworkUrlUseCase(queryParams))

        // Security
        val security = queryParams["security"]

        val sni = queryParams["sni"]
        val fingerprint = queryParams["fp"]
        val alpn = queryParams["alpn"]
        val publicKey = queryParams["pbk"]
        val shortId = queryParams["sid"]
        val spiderX = queryParams["spx"]

        when (security) {
            "tcp" -> {
                profileDataMap[ProfileField.TLS.key] = security

                sni?.let { profileDataMap[ProfileField.SNI.key] = it }
                fingerprint?.let { profileDataMap[ProfileField.Fingerprint.key] = it }
                alpn?.let { profileDataMap[ProfileField.ALPN.key] = it }
            }
            "reality" -> {
                profileDataMap[ProfileField.TLS.key] = security

                sni?.let { profileDataMap[ProfileField.SNI.key] = it }
                fingerprint?.let { profileDataMap[ProfileField.Fingerprint.key] = it }
                publicKey?.let { profileDataMap[ProfileField.PublicKey.key] = it }
                shortId?.let { profileDataMap[ProfileField.ShortID.key] = it }
                spiderX?.let { profileDataMap[ProfileField.SpiderX.key] = it }
            }
        }

        return ProfileData(
            type = ProtocolType.TROJAN,
            data = profileDataMap,
        )
    }
}
