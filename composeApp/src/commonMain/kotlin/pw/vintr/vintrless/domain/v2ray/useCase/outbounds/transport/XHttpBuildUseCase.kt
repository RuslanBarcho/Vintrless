package pw.vintr.vintrless.domain.v2ray.useCase.outbounds.transport

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.NetworkType
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object XHttpBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean.StreamSettingsBean.XhttpSettingsBean? {
        val network = profile.getField(ProfileField.TransportProtocol)
            ?: ProfileField.TransportProtocol.initialValue

        return if (network == NetworkType.SPLIT_HTTP.type || network == NetworkType.XHTTP.type) {
            val host = profile.getField(ProfileField.XHTTPHost).orEmpty()
            val path = profile.getField(ProfileField.XHTTPPath) ?: "/"
            val mode = profile.getField(ProfileField.XHTTPMode)
            val extra = profile.getField(ProfileField.XHTTPJsonExtra)

            V2RayConfig.OutboundBean.StreamSettingsBean.XhttpSettingsBean(
                host = host,
                path = path,
                mode = mode,
                extra = extra,
            )
        } else {
            null
        }
    }
}
