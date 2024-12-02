package pw.vintr.vintrless.domain.v2ray.useCase.outbounds

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object VlessOutboundBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean {
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
                network = profile.getField(ProfileField.TransportProtocol)
                    ?: ProfileField.TransportProtocol.initialValue,
                realitySettings = if (profile.getField(ProfileField.TLS) == "reality") {
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
                },
                tlsSettings = if (profile.getField(ProfileField.TLS) == "tls") {
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
                },
                security = profile.getField(ProfileField.TLS),
                tcpSettings = V2RayConfig.OutboundBean.StreamSettingsBean.TcpSettingsBean(
                    header = V2RayConfig.OutboundBean.StreamSettingsBean.TcpSettingsBean.HeaderBean(
                        type = profile.getField(ProfileField.TcpHeaderType) ?: "none"
                    )
                ),
                // TODO: http and other protocols settings
            ),
            tag = "proxy"
        )
    }
}
