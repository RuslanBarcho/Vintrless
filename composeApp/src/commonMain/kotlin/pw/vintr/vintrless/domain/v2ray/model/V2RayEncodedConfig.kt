package pw.vintr.vintrless.domain.v2ray.model

import kotlinx.serialization.Serializable

@Serializable
data class V2RayEncodedConfig(
    val id: String,
    val name: String,
    val domainName: String,
    val configJson: String,
)
