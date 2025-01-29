package pw.vintr.vintrless.domain.singbox.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingBoxConfig(
    val log: Log,
    val dns: Dns,
    val inbounds: List<Inbound>,
    val outbounds: List<Outbound>,
    val route: Route,
    val experimental: Experimental
)

@Serializable
data class Log(
    val level: String,
    val timestamp: Boolean
)

@Serializable
data class Dns(
    val servers: List<Server>,
    val rules: List<Rule>,
    val final: String
)

@Serializable
data class Server(
    val tag: String,
    val address: String,
    val strategy: String? = null,
    val detour: String? = null
)

@Serializable
data class Rule(
    @SerialName("server") val server: String,
    @SerialName("clash_mode") val clashMode: String? = null,
    @SerialName("rule_set") val ruleSet: List<String>? = null
)

@Serializable
data class Inbound(
    val type: String,
    val tag: String,
    @SerialName("interface_name") val interfaceName: String,
    @SerialName("inet4_address") val inet4Address: String,
    val mtu: Int,
    @SerialName("auto_route") val autoRoute: Boolean,
    @SerialName("strict_route") val strictRoute: Boolean,
    val stack: String,
    val sniff: Boolean
)

@Serializable
data class Outbound(
    val type: String,
    val tag: String,
    val server: String? = null,
    @SerialName("server_port") val serverPort: Int? = null,
    val version: String? = null
)

@Serializable
data class Route(
    @SerialName("auto_detect_interface") val autoDetectInterface: Boolean,
    val rules: List<RouteRule>,
    @SerialName("rule_set") val ruleSet: List<RuleSet>
)

@Serializable
data class RouteRule(
    val outbound: String,
    @SerialName("clash_mode") val clashMode: String? = null,
    val protocol: List<String>? = null,
    val port: List<Int>? = null,
    @SerialName("process_name") val processName: List<String>? = null,
    val domain: List<String>? = null,
    @SerialName("domain_suffix") val domainSuffix: List<String>? = null,
    val network: List<String>? = null,
    @SerialName("port_range") val portRange: List<String>? = null,
    @SerialName("ip_is_private") val ipIsPrivate: Boolean? = null,
    @SerialName("ip_cidr") val ipCidr: List<String>? = null,
    @SerialName("rule_set") val ruleSet: List<String>? = null
)

@Serializable
data class RuleSet(
    val tag: String,
    val type: String,
    val format: String,
    val path: String
)

@Serializable
data class Experimental(
    @SerialName("cache_file") val cacheFile: CacheFile,
    @SerialName("clash_api") val clashApi: ClashApi
)

@Serializable
data class CacheFile(
    val enabled: Boolean
)

@Serializable
data class ClashApi(
    @SerialName("external_controller") val externalController: String
)
