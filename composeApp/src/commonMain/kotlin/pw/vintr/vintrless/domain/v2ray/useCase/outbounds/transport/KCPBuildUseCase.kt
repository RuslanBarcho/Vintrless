package pw.vintr.vintrless.domain.v2ray.useCase.outbounds.transport

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.NetworkType
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object KCPBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean.StreamSettingsBean.KcpSettingsBean? {
        val network = profile.getField(ProfileField.TransportProtocol)
            ?: ProfileField.TransportProtocol.initialValue

        return if (network == NetworkType.KCP.type) {
            val headerType = profile.getField(ProfileField.KcpHeaderType) ?: "none"
            val seed = profile.getField(ProfileField.KcpSeed)

            V2RayConfig.OutboundBean.StreamSettingsBean.KcpSettingsBean(
                header = V2RayConfig.OutboundBean.StreamSettingsBean.KcpSettingsBean.HeaderBean(
                    type = headerType,
                ),
                seed = seed
            )
        } else {
            null
        }
    }
}
