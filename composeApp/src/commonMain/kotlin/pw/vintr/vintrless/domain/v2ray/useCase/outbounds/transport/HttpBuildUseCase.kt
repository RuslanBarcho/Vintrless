package pw.vintr.vintrless.domain.v2ray.useCase.outbounds.transport

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.NetworkType
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig
import pw.vintr.vintrless.tools.extensions.Comma

object HttpBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean.StreamSettingsBean.HttpSettingsBean? {
        val network = profile.getField(ProfileField.TransportProtocol)
            ?: ProfileField.TransportProtocol.initialValue

        return if (network == NetworkType.HTTP.type || network == NetworkType.H2.type) {
            val host = profile.getField(ProfileField.H2Host).orEmpty()
            val path = profile.getField(ProfileField.H2Path) ?: "/"

            V2RayConfig.OutboundBean.StreamSettingsBean.HttpSettingsBean(
                host = host
                    .split(String.Comma)
                    .map { it.trim() }
                    .filter { it.isNotEmpty() },
                path = path,
            )
        } else {
            null
        }
    }
}
