package pw.vintr.vintrless.domain.v2ray.useCase.outbounds.stream

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.V2RayConfigDefaults
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object TlsSettingsBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean.StreamSettingsBean.TlsSettingsBean? {
        return if (profile.getField(ProfileField.TLS) == V2RayConfigDefaults.TLS) {
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
    }
}
