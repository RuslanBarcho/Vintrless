package pw.vintr.vintrless.domain.profile.useCase.decodeUrl

import kotlinx.serialization.json.Json
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.profile.model.uriSchema.VmessUriSchema
import pw.vintr.vintrless.domain.v2ray.model.NetworkType
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType
import pw.vintr.vintrless.tools.extensions.Empty
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object DecodeVmessUrlUseCase {

    @OptIn(ExperimentalEncodingApi::class)
    operator fun invoke(urlString: String): ProfileData {
        val encodedString = urlString
            .replace(ProfileForm.Vmess.type.protocolScheme, String.Empty)
        val decodedUriSchema = Json.decodeFromString<VmessUriSchema>(
            Base64.decode(encodedString).decodeToString()
        )

        val profileDataMap = ProfileForm.Vmess
            .getDefaultData()
            .toMutableMap()

        profileDataMap[ProfileField.Name.key] = decodedUriSchema.ps
        profileDataMap[ProfileField.IP.key] = decodedUriSchema.add
        profileDataMap[ProfileField.Port.key] = decodedUriSchema.port
        profileDataMap[ProfileField.UserId.key] = decodedUriSchema.id
        profileDataMap[ProfileField.Security.key] = decodedUriSchema.scy

        val networkType = NetworkType.fromString(decodedUriSchema.net)

        when (networkType) {
            NetworkType.TCP -> {
                profileDataMap[ProfileField.TcpHeaderType.key] = decodedUriSchema.type
                profileDataMap[ProfileField.TcpPath.key] = decodedUriSchema.path
                profileDataMap[ProfileField.TcpHost.key] = decodedUriSchema.host
            }
            NetworkType.KCP -> {
                profileDataMap[ProfileField.KcpHeaderType.key] = decodedUriSchema.type
                profileDataMap[ProfileField.KcpSeed.key] = decodedUriSchema.path
                profileDataMap[ProfileField.KcpHost.key] = decodedUriSchema.host
            }
            NetworkType.GRPC -> {
                profileDataMap[ProfileField.GRPCMode.key] = decodedUriSchema.type
                profileDataMap[ProfileField.GRPCService.key] = decodedUriSchema.path
                profileDataMap[ProfileField.GRPCAuthority.key] = decodedUriSchema.host
            }
            NetworkType.WS -> {
                profileDataMap[ProfileField.WSPath.key] = decodedUriSchema.path
                profileDataMap[ProfileField.WSHost.key] = decodedUriSchema.host
            }
            NetworkType.HTTP_UPGRADE -> {
                profileDataMap[ProfileField.HTTPUpgradePath.key] = decodedUriSchema.path
                profileDataMap[ProfileField.HTTPUpgradeHost.key] = decodedUriSchema.host
            }
            NetworkType.SPLIT_HTTP -> {
                profileDataMap[ProfileField.HTTPUpgradePath.key] = decodedUriSchema.path
                profileDataMap[ProfileField.HTTPUpgradeHost.key] = decodedUriSchema.host
            }
            NetworkType.XHTTP -> {
                profileDataMap[ProfileField.XHTTPPath.key] = decodedUriSchema.path
                profileDataMap[ProfileField.XHTTPHost.key] = decodedUriSchema.host
            }
            NetworkType.H2 -> {
                profileDataMap[ProfileField.H2Path.key] = decodedUriSchema.path
                profileDataMap[ProfileField.H2Host.key] = decodedUriSchema.host
            }
            else -> Unit
        }

        return ProfileData(
            type = ProtocolType.VMESS,
            data = profileDataMap,
        )
    }
}
