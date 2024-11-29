package pw.vintr.vintrless.domain.v2ray.model

import kotlinx.serialization.Serializable
import pw.vintr.vintrless.domain.v2ray.Constants

@Serializable
enum class ProtocolType(
    val protocolName: String,
    val code: String,
    val protocolScheme: String,
) {
    VLESS(
        protocolName = "VLESS",
        code = "vless",
        protocolScheme = Constants.VLESS,
    ),
    VMESS(
        protocolName = "VMESS",
        code = "vmess",
        protocolScheme = Constants.VMESS,
    ),
    SHADOWSOCKS(
        protocolName = "Shadowsocks",
        code = "shadowsocks",
        protocolScheme = Constants.SHADOWSOCKS
    ),
    SOCKS(
        protocolName = "Socks",
        code = "socks",
        protocolScheme = Constants.SOCKS
    ),
    TROJAN(
        protocolName = "Trojan",
        code = "trojan",
        protocolScheme = Constants.TROJAN
    ),
    WIREGUARD(
        protocolName = "Wireguard",
        code = "wireguard",
        protocolScheme = Constants.WIREGUARD
    ),
    HYSTERIA2(
        protocolName = "Hysteria2",
        code = "hysteria2",
        protocolScheme = Constants.HYSTERIA2
    ),
    HTTP(
        protocolName = "HTTP",
        code = "http",
        protocolScheme = Constants.HTTP
    );

    companion object {
        fun getByCode(code: String) = ProtocolType.entries.find { it.code == code } ?: ProtocolType.entries.first()
    }
}
