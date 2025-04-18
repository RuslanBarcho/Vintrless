package pw.vintr.vintrless.domain.v2ray.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pw.vintr.vintrless.domain.v2ray.V2RayConfigDefaults
import pw.vintr.vintrless.tools.extensions.Empty
import pw.vintr.vintrless.tools.network.IPTools
import pw.vintr.vintrless.tools.serialization.AnySerializer

@Serializable
data class V2RayConfig(
    var remarks: String? = null,
    @Serializable(with = AnySerializer::class)
    var stats: Any? = null,
    val log: LogBean? = null,
    var policy: PolicyBean? = null,
    var inbounds: ArrayList<InboundBean> = ArrayList(),
    var outbounds: ArrayList<OutboundBean> = ArrayList(),
    var dns: DnsBean? = null,
    val routing: RoutingBean? = null,
    @Serializable(with = AnySerializer::class)
    val api: Any? = null,
    @Serializable(with = AnySerializer::class)
    val transport: Any? = null,
    @Serializable(with = AnySerializer::class)
    val reverse: Any? = null,
    @Serializable(with = AnySerializer::class)
    var fakedns: Any? = null,
    @Serializable(with = AnySerializer::class)
    val browserForwarder: Any? = null,
    @Serializable(with = AnySerializer::class)
    var observatory: Any? = null,
    @Serializable(with = AnySerializer::class)
    var burstObservatory: Any? = null
) {

    @Serializable
    data class LogBean(
        val access: String? = null,
        val error: String? = null,
        var loglevel: String?,
        val dnsLog: Boolean? = null
    )

    @Serializable
    data class InboundBean(
        var tag: String,
        var port: Int,
        var protocol: String,
        var listen: String? = null,
        val settings: InSettingsBean? = null,
        val sniffing: SniffingBean?,
        @Serializable(with = AnySerializer::class)
        val streamSettings: Any? = null,
        @Serializable(with = AnySerializer::class)
        val allocate: Any? = null
    ) {

        @Serializable
        data class InSettingsBean(
            val auth: String? = null,
            val udp: Boolean? = null,
            val userLevel: Int? = null,
            val address: String? = null,
            val port: Int? = null,
            val network: String? = null
        )

        @Serializable
        data class SniffingBean(
            var enabled: Boolean,
            val destOverride: ArrayList<String>,
            val metadataOnly: Boolean? = null,
            var routeOnly: Boolean? = null
        )
    }

    @Serializable
    data class OutboundBean(
        var tag: String = "proxy",
        var protocol: String,
        var settings: OutSettingsBean? = null,
        var streamSettings: StreamSettingsBean? = null,
        @Serializable(with = AnySerializer::class)
        val proxySettings: Any? = null,
        val sendThrough: String? = null,
        var mux: MuxBean? = MuxBean(false)
    ) {
        @Serializable
        data class OutSettingsBean(
            @SerialName("vnext")
            var vnext: List<VnextBean>? = null,
            var fragment: FragmentBean? = null,
            var noises: List<NoiseBean>? = null,
            var servers: List<ServersBean>? = null,
            /*Blackhole*/
            var response: Response? = null,
            /*DNS*/
            val network: String? = null,
            @Serializable(with = AnySerializer::class)
            var address: Any? = null,
            val port: Int? = null,
            /*Freedom*/
            var domainStrategy: String? = null,
            val redirect: String? = null,
            val userLevel: Int? = null,
            /*Loopback*/
            val inboundTag: String? = null,
            /*Wireguard*/
            var secretKey: String? = null,
            val peers: List<WireGuardBean>? = null,
            var reserved: List<Int>? = null,
            var mtu: Int? = null,
            var obfsPassword: String? = null,
        ) {

            @Serializable
            data class VnextBean(
                var address: String = "",
                @SerialName("port")
                var port: Int = V2RayConfigDefaults.DEFAULT_PORT,
                var users: List<UsersBean>
            ) {

                @Serializable
                data class UsersBean(
                    var id: String = "",
                    var alterId: Int? = null,
                    var security: String? = null,
                    var level: Int = V2RayConfigDefaults.DEFAULT_LEVEL,
                    var encryption: String? = null,
                    var flow: String? = null
                )
            }

            @Serializable
            data class FragmentBean(
                var packets: String? = null,
                var length: String? = null,
                var interval: String? = null
            )

            @Serializable
            data class NoiseBean(
                var type: String? = null,
                var packet: String? = null,
                var delay: String? = null
            )

            @Serializable
            data class ServersBean(
                var address: String = "",
                var method: String? = null,
                var ota: Boolean = false,
                var password: String? = null,
                var port: Int = V2RayConfigDefaults.DEFAULT_PORT,
                var level: Int = V2RayConfigDefaults.DEFAULT_LEVEL,
                val email: String? = null,
                var flow: String? = null,
                val ivCheck: Boolean? = null,
                var users: List<SocksUsersBean>? = null
            ) {
                @Serializable
                data class SocksUsersBean(
                    var user: String = "",
                    var pass: String = "",
                    var level: Int = V2RayConfigDefaults.DEFAULT_LEVEL
                )
            }

            @Serializable
            data class Response(var type: String)

            @Serializable
            data class WireGuardBean(
                var publicKey: String = "",
                var preSharedKey: String = "",
                var endpoint: String = ""
            )
        }

        @Serializable
        data class StreamSettingsBean(
            var network: String = V2RayConfigDefaults.DEFAULT_NETWORK,
            var security: String? = null,
            var tcpSettings: TcpSettingsBean? = null,
            var kcpSettings: KcpSettingsBean? = null,
            var wsSettings: WsSettingsBean? = null,
            var httpupgradeSettings: HttpupgradeSettingsBean? = null,
            var xhttpSettings: XhttpSettingsBean? = null,
            var httpSettings: HttpSettingsBean? = null,
            var tlsSettings: TlsSettingsBean? = null,
            var quicSettings: QuicSettingBean? = null,
            var realitySettings: TlsSettingsBean? = null,
            var grpcSettings: GrpcSettingsBean? = null,
            var hy2steriaSettings: Hysteria2SettingsBean? = null,
            @Serializable(with = AnySerializer::class)
            val dsSettings: Any? = null,
            var sockopt: SockoptBean? = null
        ) {

            @Serializable
            data class TcpSettingsBean(
                @SerialName("header")
                var header: HeaderBean = HeaderBean(),
                @SerialName("acceptProxyProtocol")
                val acceptProxyProtocol: Boolean? = null
            ) {
                @Serializable
                data class HeaderBean(
                    @SerialName("type")
                    var type: String = "none",
                    @SerialName("request")
                    var request: RequestBean? = null,
                    @SerialName("response")
                    @Serializable(with = AnySerializer::class)
                    var response: Any? = null
                ) {
                    @Serializable
                    data class RequestBean(
                        var path: List<String> = ArrayList(),
                        var headers: HeadersBean = HeadersBean(),
                        val version: String? = null,
                        val method: String? = null
                    ) {
                        @Serializable
                        data class HeadersBean(
                            @SerialName("Host")
                            var host: List<String>? = ArrayList(),
                            @SerialName("User-Agent")
                            val userAgent: List<String>? = null,
                            @SerialName("Accept-Encoding")
                            val acceptEncoding: List<String>? = null,
                            @SerialName("Connection")
                            val connection: List<String>? = null,
                            @SerialName("Pragma")
                            val pragma: String? = null
                        )
                    }
                }
            }

            @Serializable
            data class KcpSettingsBean(
                var mtu: Int = 1350,
                var tti: Int = 50,
                var uplinkCapacity: Int = 12,
                var downlinkCapacity: Int = 100,
                var congestion: Boolean = false,
                var readBufferSize: Int = 1,
                var writeBufferSize: Int = 1,
                var header: HeaderBean = HeaderBean(),
                var seed: String? = null
            ) {
                @Serializable
                data class HeaderBean(var type: String = "none")
            }

            @Serializable
            data class WsSettingsBean(
                var path: String? = null,
                var headers: HeadersBean = HeadersBean(),
                val maxEarlyData: Int? = null,
                val useBrowserForwarding: Boolean? = null,
                val acceptProxyProtocol: Boolean? = null
            ) {
                @Serializable
                data class HeadersBean(
                    @SerialName("Host")
                    var host: String = String.Empty
                )
            }

            @Serializable
            data class HttpupgradeSettingsBean(
                var path: String? = null,
                var host: String? = null,
                val acceptProxyProtocol: Boolean? = null
            )

            @Serializable
            data class XhttpSettingsBean(
                var path: String? = null,
                var host: String? = null,
                var mode: String? = null,
                @Serializable(with = AnySerializer::class)
                var extra: Any? = null,
            )

            @Serializable
            data class HttpSettingsBean(
                var host: List<String> = ArrayList(),
                var path: String? = null
            )

            @Serializable
            data class SockoptBean(
                @SerialName("TcpNoDelay")
                var tcpNoDelay: Boolean? = null,
                var tcpKeepAliveIdle: Int? = null,
                var tcpFastOpen: Boolean? = null,
                var tproxy: String? = null,
                var mark: Int? = null,
                var dialerProxy: String? = null
            )

            @Serializable
            data class TlsSettingsBean(
                var allowInsecure: Boolean = false,
                var serverName: String? = null,
                val alpn: List<String>? = null,
                val minVersion: String? = null,
                val maxVersion: String? = null,
                val preferServerCipherSuites: Boolean? = null,
                val cipherSuites: String? = null,
                val fingerprint: String? = null,
                val certificates: List<@Serializable(with = AnySerializer::class) Any?>? = null,
                val disableSystemRoot: Boolean? = null,
                val enableSessionResumption: Boolean? = null,
                // REALITY settings
                val show: Boolean = false,
                var publicKey: String? = null,
                var shortId: String? = null,
                var spiderX: String? = null
            )

            @Serializable
            data class QuicSettingBean(
                var security: String = "none",
                var key: String = "",
                var header: HeaderBean = HeaderBean()
            ) {
                @Serializable
                data class HeaderBean(var type: String = "none")
            }

            @Serializable
            data class GrpcSettingsBean(
                var serviceName: String = "",
                var authority: String? = null,
                var multiMode: Boolean? = null,
                @SerialName("idle_timeout")
                var idleTimeout: Int? = null,
                @SerialName("health_check_timeout")
                var healthCheckTimeout: Int? = null
            )

            @Serializable
            data class Hysteria2SettingsBean(
                @SerialName("password")
                var password: String? = null,
                @SerialName("use_udp_extension")
                var useUdpExtension: Boolean? = true,
                @SerialName("congestion")
                var congestion: Hy2CongestionBean? = null
            ) {
                @Serializable
                data class Hy2CongestionBean(
                    var type: String? = "bbr",
                    @SerialName("up_mbps")
                    var upMbps: Int? = null,
                    @SerialName("down_mbps")
                    var downMbps: Int? = null,
                )
            }
        }

        @Serializable
        data class MuxBean(
            var enabled: Boolean,
            var concurrency: Int = 8,
            @SerialName("xudpConcurrency")
            var xudpConcurrency: Int? = null,
            @SerialName("xudpProxyUDP443")
            var xudpProxyUDP443: String? = null,
        )

        fun getServerAddress(): String? {
            if (
                protocol.equals(ProtocolType.VMESS.name, true) ||
                protocol.equals(ProtocolType.VLESS.name, true)
            ) {
                return settings?.vnext?.first()?.address
            } else if (
                protocol.equals(ProtocolType.SHADOWSOCKS.name, true) ||
                protocol.equals(ProtocolType.SOCKS.name, true) ||
                protocol.equals(ProtocolType.HTTP.name, true) ||
                protocol.equals(ProtocolType.TROJAN.name, true) ||
                protocol.equals(ProtocolType.HYSTERIA2.name, true)
            ) {
                return settings?.servers?.first()?.address
            } else if (protocol.equals(ProtocolType.WIREGUARD.name, true)) {
                return settings?.peers?.first()?.endpoint?.substringBeforeLast(":")
            }
            return null
        }

        fun getServerPort(): Int? {
            if (
                protocol.equals(ProtocolType.VMESS.code, true) ||
                protocol.equals(ProtocolType.VLESS.code, true)
            ) {
                return settings?.vnext?.first()?.port
            } else if (
                protocol.equals(ProtocolType.SHADOWSOCKS.code, true) ||
                protocol.equals(ProtocolType.SOCKS.code, true) ||
                protocol.equals(ProtocolType.HTTP.code, true) ||
                protocol.equals(ProtocolType.TROJAN.code, true) ||
                protocol.equals(ProtocolType.HYSTERIA2.code, true)
            ) {
                return settings?.servers?.first()?.port
            } else if (protocol.equals(ProtocolType.WIREGUARD.code, true)) {
                return settings?.peers?.first()?.endpoint?.substringAfterLast(":")?.toInt()
            }
            return null
        }

        fun getServerAddressAndPort(): String {
            val address = getServerAddress().orEmpty()
            val port = getServerPort()
            return IPTools.getIpv6Address(address) + ":" + port
        }

        fun getPassword(): String? {
            if (
                protocol.equals(ProtocolType.VMESS.code, true) ||
                protocol.equals(ProtocolType.VLESS.code, true)
            ) {
                return settings?.vnext?.first()?.users?.first()?.id
            } else if (
                protocol.equals(ProtocolType.SHADOWSOCKS.code, true) ||
                protocol.equals(ProtocolType.TROJAN.code, true) ||
                protocol.equals(ProtocolType.HYSTERIA2.code, true)
            ) {
                return settings?.servers?.first()?.password
            } else if (
                protocol.equals(ProtocolType.SOCKS.code, true) ||
                protocol.equals(ProtocolType.HTTP.code, true)
            ) {
                return settings?.servers?.first()?.users?.first()?.pass
            } else if (protocol.equals(ProtocolType.WIREGUARD.code, true)) {
                return settings?.secretKey
            }
            return null
        }

        fun getSecurityEncryption(): String? {
            return when {
                protocol.equals(ProtocolType.VMESS.code, true) -> {
                    settings?.vnext?.first()?.users?.first()?.security
                }
                protocol.equals(ProtocolType.VLESS.code, true) -> {
                    settings?.vnext?.first()?.users?.first()?.encryption
                }
                protocol.equals(ProtocolType.SHADOWSOCKS.code, true) -> {
                    settings?.servers?.first()?.method
                }
                else -> null
            }
        }

        fun getTransportSettingDetails(): List<String?>? {
            if (
                protocol.equals(ProtocolType.VMESS.code, true) ||
                protocol.equals(ProtocolType.VLESS.code, true) ||
                protocol.equals(ProtocolType.TROJAN.code, true) ||
                protocol.equals(ProtocolType.SHADOWSOCKS.code, true)
            ) {
                val transport = streamSettings?.network ?: return null
                return when (transport) {
                    NetworkType.TCP.type -> {
                        val tcpSetting = streamSettings?.tcpSettings ?: return null
                        listOf(
                            tcpSetting.header.type,
                            tcpSetting.header.request?.headers?.host?.joinToString(",").orEmpty(),
                            tcpSetting.header.request?.path?.joinToString(",").orEmpty()
                        )
                    }

                    NetworkType.KCP.type -> {
                        val kcpSetting = streamSettings?.kcpSettings ?: return null
                        listOf(
                            kcpSetting.header.type,
                            "",
                            kcpSetting.seed.orEmpty()
                        )
                    }

                    NetworkType.WS.type -> {
                        val wsSetting = streamSettings?.wsSettings ?: return null
                        listOf(
                            "",
                            wsSetting.headers.host,
                            wsSetting.path
                        )
                    }

                    NetworkType.HTTP_UPGRADE.type -> {
                        val httpupgradeSetting = streamSettings?.httpupgradeSettings ?: return null
                        listOf(
                            "",
                            httpupgradeSetting.host,
                            httpupgradeSetting.path
                        )
                    }

                    NetworkType.SPLIT_HTTP.type, NetworkType.XHTTP.type -> {
                        val xhttpSettings = streamSettings?.xhttpSettings ?: return null
                        listOf(
                            "",
                            xhttpSettings.host,
                            xhttpSettings.path
                        )
                    }

                    NetworkType.H2.type -> {
                        val h2Setting = streamSettings?.httpSettings ?: return null
                        listOf(
                            "",
                            h2Setting.host.joinToString(","),
                            h2Setting.path
                        )
                    }

                    NetworkType.GRPC.type -> {
                        val grpcSetting = streamSettings?.grpcSettings ?: return null
                        listOf(
                            if (grpcSetting.multiMode == true) "multi" else "gun",
                            grpcSetting.authority.orEmpty(),
                            grpcSetting.serviceName
                        )
                    }

                    else -> null
                }
            }
            return null
        }
    }

    @Serializable
    data class DnsBean(
        var servers: ArrayList<@Serializable(with = AnySerializer::class) Any?>? = null,
        var hosts: Map<String, @Serializable(with = AnySerializer::class) Any?>? = null,
        val clientIp: String? = null,
        val disableCache: Boolean? = null,
        val queryStrategy: String? = null,
        val tag: String? = null
    ) {
        @Serializable
        data class ServersBean(
            var address: String = "",
            var port: Int? = null,
            var domains: List<String>? = null,
            var expectIPs: List<String>? = null,
            val clientIp: String? = null,
            val skipFallback: Boolean? = null,
        )
    }

    @Serializable
    data class RoutingBean(
        var domainStrategy: String,
        var domainMatcher: String? = null,
        var rules: ArrayList<RulesBean>,
        val balancers: List<@Serializable(with = AnySerializer::class) Any?>? = null
    ) {

        @Serializable
        data class RulesBean(
            @SerialName("type")
            var type: String?,
            var ip: ArrayList<String>? = null,
            var domain: ArrayList<String>? = null,
            var outboundTag: String = "",
            var balancerTag: String? = null,
            var port: String? = null,
            val sourcePort: String? = null,
            val network: String? = null,
            val source: List<String>? = null,
            val user: List<String>? = null,
            var inboundTag: List<String>? = null,
            val protocol: List<String>? = null,
            val attrs: String? = null,
            val domainMatcher: String? = null
        )
    }

    @Serializable
    data class PolicyBean(
        var levels: Map<String, LevelBean>,
        @Serializable(with = AnySerializer::class)
        var system: Any? = null
    ) {
        @Serializable
        data class LevelBean(
            var handshake: Int? = null,
            var connIdle: Int? = null,
            var uplinkOnly: Int? = null,
            var downlinkOnly: Int? = null,
            val statsUserUplink: Boolean? = null,
            val statsUserDownlink: Boolean? = null,
            var bufferSize: Int? = null
        )
    }

    data class FakednsBean(
        var ipPool: String = "198.18.0.0/15",
        var poolSize: Int = 10000
    ) // roughly 10 times smaller than total ip pool

    fun getProxyOutbound(): OutboundBean? {
        outbounds.forEach { outbound ->
            ProtocolType.entries.forEach {
                if (outbound.protocol.equals(it.protocolName, true)) {
                    return outbound
                }
            }
        }
        return null
    }

    fun toJson(): String {
        val json = Json {
            encodeDefaults = true
            explicitNulls = false
        }
        return json.encodeToString(value = this)
    }
}
