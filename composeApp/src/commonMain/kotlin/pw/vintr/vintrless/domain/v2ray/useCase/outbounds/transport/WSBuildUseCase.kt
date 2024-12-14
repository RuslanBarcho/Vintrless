package pw.vintr.vintrless.domain.v2ray.useCase.outbounds.transport

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.NetworkType
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object WSBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean.StreamSettingsBean.WsSettingsBean? {
        val network = profile.getField(ProfileField.TransportProtocol)
            ?: ProfileField.TransportProtocol.initialValue

        return if (network == NetworkType.WS.type) {
            val host = profile.getField(ProfileField.WSHost).orEmpty()
            val path = profile.getField(ProfileField.WSPath) ?: "/"

            V2RayConfig.OutboundBean.StreamSettingsBean.WsSettingsBean(
                headers = V2RayConfig.OutboundBean.StreamSettingsBean.WsSettingsBean.HeadersBean(
                    host = host,
                ),
                path = path,
            )
        } else {
            null
        }
    }
}
