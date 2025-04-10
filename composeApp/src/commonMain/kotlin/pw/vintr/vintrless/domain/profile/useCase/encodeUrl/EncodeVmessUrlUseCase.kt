package pw.vintr.vintrless.domain.profile.useCase.encodeUrl

import kotlinx.serialization.json.Json
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.profile.model.uriSchema.VmessUriSchema
import pw.vintr.vintrless.domain.profile.useCase.encodeUrl.base.BaseEncodeProfileUrlUseCase
import pw.vintr.vintrless.domain.v2ray.model.NetworkType
import pw.vintr.vintrless.tools.extensions.Empty
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object EncodeVmessUrlUseCase : BaseEncodeProfileUrlUseCase() {

    @OptIn(ExperimentalEncodingApi::class)
    override operator fun invoke(profile: ProfileData): String {
        val form = ProfileForm.getByType(profile.type)
        val networkType = NetworkType.fromString(profile.data[ProfileField.TransportProtocol.key].orEmpty())

        var type = String.Empty
        var path = String.Empty
        var host = String.Empty

        when (networkType) {
            NetworkType.TCP -> {
                type = profile.data[ProfileField.TcpHeaderType.key].orEmpty()
                path = profile.data[ProfileField.TcpPath.key].orEmpty()
                host = profile.data[ProfileField.TcpHost.key].orEmpty()
            }
            NetworkType.KCP -> {
                type = profile.data[ProfileField.KcpHeaderType.key].orEmpty()
                path = profile.data[ProfileField.KcpSeed.key].orEmpty()
                host = profile.data[ProfileField.KcpHost.key].orEmpty()
            }
            NetworkType.GRPC -> {
                type = profile.data[ProfileField.GRPCMode.key].orEmpty()
                path = profile.data[ProfileField.GRPCService.key].orEmpty()
                host = profile.data[ProfileField.GRPCAuthority.key].orEmpty()
            }
            NetworkType.WS -> {
                path = profile.data[ProfileField.WSPath.key].orEmpty()
                host = profile.data[ProfileField.WSHost.key].orEmpty()
            }
            NetworkType.HTTP_UPGRADE -> {
                path = profile.data[ProfileField.HTTPUpgradePath.key].orEmpty()
                host = profile.data[ProfileField.HTTPUpgradeHost.key].orEmpty()
            }
            NetworkType.SPLIT_HTTP -> {
                path = profile.data[ProfileField.HTTPUpgradePath.key].orEmpty()
                host = profile.data[ProfileField.HTTPUpgradeHost.key].orEmpty()
            }
            NetworkType.XHTTP -> {
                path = profile.data[ProfileField.XHTTPPath.key].orEmpty()
                host = profile.data[ProfileField.XHTTPHost.key].orEmpty()
            }
            NetworkType.H2 -> {
                path = profile.data[ProfileField.H2Path.key].orEmpty()
                host = profile.data[ProfileField.H2Host.key].orEmpty()
            }
            else -> Unit
        }

        val schema = VmessUriSchema(
            v = "2",
            ps = profile.name,
            add = profile.ip,
            port = profile.data[ProfileField.Port.key].orEmpty(),
            id = profile.data[ProfileField.UserId.key].orEmpty(),
            scy = profile.data[ProfileField.VmessSecurity.key].orEmpty(),
            aid = "0",
            net = networkType.type,
            type = type,
            path = path,
            host = host,
            tls = profile.data[ProfileField.TLS.key].orEmpty(),
            sni = profile.data[ProfileField.SNI.key].orEmpty(),
            fp = profile.data[ProfileField.Fingerprint.key].orEmpty(),
            alpn = profile.data[ProfileField.ALPN.key].orEmpty()
        )
        val json = Json {
            encodeDefaults = true
            explicitNulls = false
        }
        val jsonEncodedSchema = json.encodeToString(schema)

        return "${form.type.protocolScheme}${Base64.encode(jsonEncodedSchema.encodeToByteArray())}"
    }
}
