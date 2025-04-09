package pw.vintr.vintrless.domain.v2ray.useCase.outbounds

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object SocksOutboundBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean {
        // Build outbounds
        val username = profile.getField(ProfileField.UserName).orEmpty()
        val password = profile.getField(ProfileField.Password).orEmpty()

        return V2RayConfig.OutboundBean(
            mux = V2RayConfig.OutboundBean.MuxBean(
                concurrency = -1,
                enabled = false,
            ),
            protocol = profile.type.code,
            settings = V2RayConfig.OutboundBean.OutSettingsBean(
                servers = listOf(
                    V2RayConfig.OutboundBean.OutSettingsBean.ServersBean(
                        address = profile.getField(ProfileField.IP).orEmpty(),
                        port = profile.getField(ProfileField.Port)?.toIntOrNull() ?: 443,
                        users = if (username.isNotEmpty()) {
                            listOf(
                                V2RayConfig.OutboundBean.OutSettingsBean.ServersBean.SocksUsersBean(
                                    user = username,
                                    pass = password,
                                )
                            )
                        } else null,
                    )
                )
            ),
            streamSettings = V2RayConfig.OutboundBean.StreamSettingsBean(
                network = ProfileField.TransportProtocol.initialValue,
            ),
            tag = "proxy"
        )
    }
}
