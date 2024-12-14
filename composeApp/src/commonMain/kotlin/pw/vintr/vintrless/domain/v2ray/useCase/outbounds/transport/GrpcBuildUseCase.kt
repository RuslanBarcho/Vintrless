package pw.vintr.vintrless.domain.v2ray.useCase.outbounds.transport

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.NetworkType
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object GrpcBuildUseCase {

    private const val IDLE_TIMEOUT = 60
    private const val HEALTH_CHECK_TIMEOUT = 20

    operator fun invoke(profile: ProfileData): V2RayConfig.OutboundBean.StreamSettingsBean.GrpcSettingsBean? {
        val network = profile.getField(ProfileField.TransportProtocol)
            ?: ProfileField.TransportProtocol.initialValue

        return if (network == NetworkType.GRPC.type) {
            val multiMode = profile.getField(ProfileField.GRPCMode) == "multi"
            val serviceName = profile.getField(ProfileField.GRPCService).orEmpty()
            val authority = profile.getField(ProfileField.GRPCAuthority)

            V2RayConfig.OutboundBean.StreamSettingsBean.GrpcSettingsBean(
                multiMode = multiMode,
                serviceName = serviceName,
                authority = authority,
                idleTimeout = IDLE_TIMEOUT,
                healthCheckTimeout = HEALTH_CHECK_TIMEOUT,
            )
        } else {
            null
        }
    }
}
