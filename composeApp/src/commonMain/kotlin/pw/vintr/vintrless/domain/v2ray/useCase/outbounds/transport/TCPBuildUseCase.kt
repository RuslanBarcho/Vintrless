package pw.vintr.vintrless.domain.v2ray.useCase.outbounds.transport

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.V2RayConfigDefaults
import pw.vintr.vintrless.domain.v2ray.model.NetworkType
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object TCPBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean.StreamSettingsBean.TcpSettingsBean? {
        val network = profile.getField(ProfileField.TransportProtocol)
            ?: ProfileField.TransportProtocol.initialValue

        return if (network == NetworkType.TCP.type) {
            val headerType = profile.getField(ProfileField.TcpHeaderType) ?: "none"

            val request = if (headerType == V2RayConfigDefaults.HEADER_TYPE_HTTP) {
                val host = profile.getField(ProfileField.TcpHost)
                val path = profile.getField(ProfileField.TcpPath)

                V2RayConfig.OutboundBean.StreamSettingsBean.TcpSettingsBean.HeaderBean.RequestBean(
                    headers = V2RayConfig.OutboundBean.StreamSettingsBean.TcpSettingsBean.HeaderBean.RequestBean.HeadersBean(
                        host = host.orEmpty()
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                    ),
                    path = path.orEmpty()
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                )
            } else {
                null
            }

            V2RayConfig.OutboundBean.StreamSettingsBean.TcpSettingsBean(
                header = V2RayConfig.OutboundBean.StreamSettingsBean.TcpSettingsBean.HeaderBean(
                    type = profile.getField(ProfileField.TcpHeaderType) ?: "none",
                    request = request,
                ),
            )
        } else {
            null
        }
    }
}
