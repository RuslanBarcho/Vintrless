package pw.vintr.vintrless.domain.v2ray.useCase.outbounds

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig
import pw.vintr.vintrless.domain.v2ray.useCase.outbounds.stream.RealitySettingBuildUseCase
import pw.vintr.vintrless.domain.v2ray.useCase.outbounds.stream.TlsSettingsBuildUseCase
import pw.vintr.vintrless.domain.v2ray.useCase.outbounds.transport.*

object VmessOutboundBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean {
        val network = profile.getField(ProfileField.TransportProtocol)
            ?: ProfileField.TransportProtocol.initialValue

        // Build outbounds
        return V2RayConfig.OutboundBean(
            mux = V2RayConfig.OutboundBean.MuxBean(
                concurrency = -1,
                enabled = false,
            ),
            protocol = profile.type.code,
            settings = V2RayConfig.OutboundBean.OutSettingsBean(
                vnext = listOf(
                    V2RayConfig.OutboundBean.OutSettingsBean.VnextBean(
                        address = profile.getField(ProfileField.IP).orEmpty(),
                        port = profile.getField(ProfileField.Port)?.toIntOrNull() ?: 443,
                        users = listOf(V2RayConfig.OutboundBean.OutSettingsBean.VnextBean.UsersBean(
                            id = profile.getField(ProfileField.UserId).orEmpty(),
                            level = 8,
                            security = profile.getField(ProfileField.VmessSecurity).orEmpty(),
                        )),
                    )
                )
            ),
            streamSettings = V2RayConfig.OutboundBean.StreamSettingsBean(
                network = network,
                realitySettings = RealitySettingBuildUseCase(profile),
                tlsSettings = TlsSettingsBuildUseCase(profile),
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
