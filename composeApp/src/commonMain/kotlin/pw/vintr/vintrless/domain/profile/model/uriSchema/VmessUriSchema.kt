package pw.vintr.vintrless.domain.profile.model.uriSchema

import kotlinx.serialization.Serializable
import pw.vintr.vintrless.tools.extensions.Empty

@Serializable
data class VmessUriSchema(
    val v: String = String.Empty,
    val ps: String = String.Empty,
    val add: String = String.Empty,
    val port: String = String.Empty,
    val id: String = String.Empty,
    val aid: String = String.Empty,
    val scy: String = String.Empty,
    val net: String = String.Empty,
    val type: String = String.Empty,
    val host: String = String.Empty,
    val path: String = String.Empty,
    val tls: String = String.Empty,
    val sni: String = String.Empty,
    val alpn: String = String.Empty,
    val fp: String = String.Empty,
)
