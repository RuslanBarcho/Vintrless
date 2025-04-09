package pw.vintr.vintrless.domain.profile.useCase.decodeUrl

import com.eygraber.uri.UriCodec
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object DecodeShadowsocksUrlUseCase {

    private val partExtractRegex = """^ss://([^@]+)@([^:]+):(\d+)#(.+)$""".toRegex()

    @OptIn(ExperimentalEncodingApi::class)
    operator fun invoke(urlString: String): ProfileData {
        val urlParts = partExtractRegex.matchEntire(urlString) ?: throw Exception("Bad url format")

        val authInfo = Base64.decode(urlParts.groupValues[1])
            .decodeToString()
            .split(":")
        val ip = urlParts.groupValues[2]
        val port = urlParts.groupValues[3]
        val name = UriCodec.decode(urlParts.groupValues[4])

        val profileDataMap = ProfileForm.ShadowSocks
            .getDefaultData()
            .toMutableMap()

        profileDataMap[ProfileField.SSocksSecurity.key] = authInfo[0]
        profileDataMap[ProfileField.Password.key] = authInfo[1]
        profileDataMap[ProfileField.IP.key] = ip
        profileDataMap[ProfileField.Port.key] = port
        profileDataMap[ProfileField.Name.key] = name

        return ProfileData(
            type = ProtocolType.SHADOWSOCKS,
            data = profileDataMap,
        )
    }
}
