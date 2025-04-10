package pw.vintr.vintrless.domain.v2ray.useCase.outbounds

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig.OutboundBean.OutSettingsBean.WireGuardBean
import pw.vintr.vintrless.tools.network.IPTools

object WireguardOutboundBuildUseCase {

    private const val DEFAULT_WIREGUARD_LOCAL_ADDRESS_V4 = "172.16.0.2/32"

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean {
        // Build outbounds
        return V2RayConfig.OutboundBean(
            protocol = profile.type.code,
            settings = V2RayConfig.OutboundBean.OutSettingsBean(
                secretKey = profile.getField(ProfileField.ServerPrivateKey),
                address = (profile.getField(ProfileField.LocalAddress) ?: DEFAULT_WIREGUARD_LOCAL_ADDRESS_V4)
                    .split(","),
                peers = listOf(
                    WireGuardBean(
                        publicKey = profile.getField(ProfileField.ServerPublicKey).orEmpty(),
                        preSharedKey = profile.getField(ProfileField.ServerAdditionalKey).orEmpty(),
                        endpoint = buildString {
                            append(IPTools.getIpv6Address(profile.getField(ProfileField.IP)))
                            append(":")
                            append(profile.getField(ProfileField.Port))
                        },
                    ),
                ),
                mtu = profile.getField(ProfileField.MTU)?.toIntOrNull(),
                reserved = profile.getField(ProfileField.Reserved)
                    ?.split(",")
                    ?.mapNotNull { it.toIntOrNull() }
            ),
            tag = "proxy"
        )
    }
}
