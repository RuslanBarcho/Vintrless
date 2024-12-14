package pw.vintr.vintrless.domain.v2ray.useCase.outbounds

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.V2RayConfigDefaults
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig
import pw.vintr.vintrless.domain.v2ray.useCase.outbounds.transport.*

object VlessOutboundBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean {
        val network = profile.getField(ProfileField.TransportProtocol)
            ?: ProfileField.TransportProtocol.initialValue

        // Build tls or reality settings
        val realitySettings = if (profile.getField(ProfileField.TLS) == V2RayConfigDefaults.REALITY) {
            V2RayConfig.OutboundBean.StreamSettingsBean.TlsSettingsBean(
                allowInsecure = false,
                serverName = profile.getField(ProfileField.SNI),
                fingerprint = profile.getField(ProfileField.Fingerprint),
                publicKey = profile.getField(ProfileField.PublicKey),
                shortId = profile.getField(ProfileField.ShortID),
                spiderX = profile.getField(ProfileField.SpiderX),
            )
        } else {
            null
        }
        val tlsSettings = if (profile.getField(ProfileField.TLS) == V2RayConfigDefaults.TLS) {
            V2RayConfig.OutboundBean.StreamSettingsBean.TlsSettingsBean(
                allowInsecure = profile.getField(ProfileField.AllowInsecure).toBoolean(),
                serverName = profile.getField(ProfileField.SNI),
                fingerprint = profile.getField(ProfileField.Fingerprint),
                alpn = profile.getField(ProfileField.ALPN)
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
            )
        } else {
            null
        }

        // Build outbounds
        return V2RayConfig.OutboundBean(
            mux = V2RayConfig.OutboundBean.MuxBean(
                concurrency = -1,
                enabled = false,
                xudpConcurrency = 8,
                xudpProxyUDP443 = ""
            ),
            protocol = profile.type.code,
            settings = V2RayConfig.OutboundBean.OutSettingsBean(
                vnext = listOf(
                    V2RayConfig.OutboundBean.OutSettingsBean.VnextBean(
                        address = profile.getField(ProfileField.IP).orEmpty(),
                        port = profile.getField(ProfileField.Port)?.toIntOrNull() ?: 443,
                        users = listOf(V2RayConfig.OutboundBean.OutSettingsBean.VnextBean.UsersBean(
                            encryption = profile.getField(ProfileField.Encryption)
                                ?: ProfileField.Encryption.initialValue,
                            flow = profile.getField(ProfileField.Flow),
                            id = profile.getField(ProfileField.UserId).orEmpty(),
                            level = 8,
                        )),
                    )
                )
            ),
            streamSettings = V2RayConfig.OutboundBean.StreamSettingsBean(
                network = network,
                realitySettings = realitySettings,
                tlsSettings = tlsSettings,
                security = profile.getField(ProfileField.TLS),
                tcpSettings = TCPBuildUseCase(profile),
                kcpSettings = KCPBuildUseCase(profile),
                wsSettings = WSBuildUseCase(profile),
                httpupgradeSettings = HttpUpgradeBuildUseCase(profile),
                xhttpSettings = XHttpBuildUseCase(profile),
                httpSettings = HttpBuildUseCase(profile),
                grpcSettings = GrpcBuildUseCase(profile),
            ),
            tag = "proxy"
        )
    }
}
