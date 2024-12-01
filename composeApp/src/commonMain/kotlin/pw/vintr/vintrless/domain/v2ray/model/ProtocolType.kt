package pw.vintr.vintrless.domain.v2ray.model

import kotlinx.serialization.Serializable
import pw.vintr.vintrless.domain.v2ray.V2RayConfigDefaults

@Serializable
enum class ProtocolType(
    val protocolName: String,
    val code: String,
    val protocolScheme: String,
) {
    VLESS(
        protocolName = "VLESS",
        code = "vless",
        protocolScheme = V2RayConfigDefaults.VLESS,
    ),
    VMESS(
        protocolName = "VMESS",
        code = "vmess",
        protocolScheme = V2RayConfigDefaults.VMESS,
    ),
    SHADOWSOCKS(
        protocolName = "Shadowsocks",
        code = "shadowsocks",
        protocolScheme = V2RayConfigDefaults.SHADOWSOCKS
    ),
    SOCKS(
        protocolName = "Socks",
        code = "socks",
        protocolScheme = V2RayConfigDefaults.SOCKS
    ),
    TROJAN(
        protocolName = "Trojan",
        code = "trojan",
        protocolScheme = V2RayConfigDefaults.TROJAN
    ),
    WIREGUARD(
        protocolName = "Wireguard",
        code = "wireguard",
        protocolScheme = V2RayConfigDefaults.WIREGUARD
    ),
    HYSTERIA2(
        protocolName = "Hysteria2",
        code = "hysteria2",
        protocolScheme = V2RayConfigDefaults.HYSTERIA2
    ),
    HTTP(
        protocolName = "HTTP",
        code = "http",
        protocolScheme = V2RayConfigDefaults.HTTP
    );

    companion object {
        fun getByCode(code: String) = ProtocolType.entries.find { it.code == code } ?: ProtocolType.entries.first()
    }
}
