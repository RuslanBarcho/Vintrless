package pw.vintr.vintrless.domain.v2ray.useCase.outbounds.stream

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.V2RayConfigDefaults
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object RealitySettingBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean.StreamSettingsBean.TlsSettingsBean? {
        return if (profile.getField(ProfileField.TLS) == V2RayConfigDefaults.REALITY) {
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
    }
}
