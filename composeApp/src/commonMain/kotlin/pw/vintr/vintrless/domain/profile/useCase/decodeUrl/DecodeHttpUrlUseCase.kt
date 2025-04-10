package pw.vintr.vintrless.domain.profile.useCase.decodeUrl

import com.eygraber.uri.UriCodec
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType

object DecodeHttpUrlUseCase {

    private val partExtractRegex = """^http://([^@]+)@([^:]+):(\d+)#(.+)$""".toRegex()

    operator fun invoke(urlString: String): ProfileData {
        val urlParts = partExtractRegex.matchEntire(urlString) ?: throw Exception("Bad url format")

        val authInfo = UriCodec.decode(urlParts.groupValues[1])
            .split(":")
        val ip = urlParts.groupValues[2]
        val port = urlParts.groupValues[3]
        val name = UriCodec.decode(urlParts.groupValues[4])

        val profileDataMap = ProfileForm.Http
            .getDefaultData()
            .toMutableMap()

        profileDataMap[ProfileField.UserName.key] = authInfo.getOrNull(0).orEmpty()
        profileDataMap[ProfileField.Password.key] = authInfo.getOrNull(1).orEmpty()
        profileDataMap[ProfileField.IP.key] = ip
        profileDataMap[ProfileField.Port.key] = port
        profileDataMap[ProfileField.Name.key] = name

        return ProfileData(
            type = ProtocolType.HTTP,
            data = profileDataMap,
        )
    }
}
