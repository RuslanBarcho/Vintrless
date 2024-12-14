package pw.vintr.vintrless.domain.v2ray.useCase.outbounds.transport

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.NetworkType
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object HttpUpgradeBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean.StreamSettingsBean.HttpupgradeSettingsBean? {
        val network = profile.getField(ProfileField.TransportProtocol)
            ?: ProfileField.TransportProtocol.initialValue

        return if (network == NetworkType.HTTP_UPGRADE.type) {
            val host = profile.getField(ProfileField.HTTPUpgradeHost).orEmpty()
            val path = profile.getField(ProfileField.HTTPUpgradePath) ?: "/"

            V2RayConfig.OutboundBean.StreamSettingsBean.HttpupgradeSettingsBean(
                host = host,
                path = path,
            )
        } else {
            null
        }
    }
}
