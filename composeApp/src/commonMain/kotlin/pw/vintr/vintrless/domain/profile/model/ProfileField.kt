package pw.vintr.vintrless.domain.profile.model

import org.jetbrains.compose.resources.StringResource
import pw.vintr.vintrless.tools.extensions.Empty
import vintrless.composeapp.generated.resources.*

sealed class ProfileField {

    open val availableValues: List<String?>? = null

    open val initialValue: String? = null

    open val subfieldsByValue: Map<String?, List<ProfileField>> = mapOf()

    open val multiline: Boolean = false

    abstract val titleRes: StringResource

    abstract val key: String

    open val queryKey: String = String.Empty

    data object Name : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_name
        override val key: String = "name"
    }

    data object IP : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_ip
        override val key: String = "ip"
    }

    data object Port : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_port
        override val key: String = "port"
    }

    data object UserId : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_user_id
        override val key: String = "userId"
    }

    data object UserName : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_user
        override val key: String = "username"
    }

    data object Password : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_password
        override val key: String = "password"
    }

    data object ServerPrivateKey : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_server_private_key
        override val key: String = "server-private-key"
    }

    data object ServerPublicKey : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_server_public_key
        override val key: String = "server-public-key"
        override val queryKey: String = "publickey"
    }

    data object ServerAdditionalKey : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_server_additional_key
        override val key: String = "server-additional-key"
        override val queryKey: String = "presharedkey"
    }

    data object Reserved : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_reserved
        override val initialValue = "0,0,0"
        override val key: String = "reserved"
        override val queryKey: String = "reserved"
    }

    data object LocalAddress : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_local_address
        override val initialValue = "172.16.0.2/32,2606:4700:110:8f81:d551:a0:532e:a2b3/128"
        override val key: String = "address"
        override val queryKey: String = "address"
    }

    data object MTU : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_mtu
        override val initialValue = "1420"
        override val key: String = "mtu"
        override val queryKey: String = "mtu"
    }

    data object Flow : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_flow

        override val availableValues = listOf(
            null,
            "xtls-rprx-vision",
            "xtls-rprx-vision-udp443"
        )

        override val key: String = "flow"
        override val queryKey: String = "flow"
    }

    data object Encryption : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_encryption
        override val initialValue = "none"
        override val key: String = "encryption"
        override val queryKey: String = "encryption"
    }

    data object VmessSecurity : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_security

        override val availableValues = listOf(
            "chacha20-poly1305",
            "aes-128-gcm",
            "auto",
            "none",
            "zero"
        )

        override val initialValue = availableValues.first()

        override val key: String = "vm-security"
        override val queryKey: String = "scy"
    }

    data object SSocksSecurity : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_security

        override val availableValues = listOf(
            "aes-256-gcm",
            "aes-128-gcm",
            "chacha20-poly1305",
            "chacha20-ietf-poly1305",
            "xchacha20-poly1305",
            "xchacha20-ietf-poly1305",
            "none",
            "plain",
            "2022-blake3-aes-128-gcm",
            "2022-blake3-aes-256-gcm",
            "2022-blake3-chacha20-poly1305"
        )

        override val initialValue = availableValues.first()

        override val key: String = "ss-security"
        override val queryKey: String = "method"
    }

    /**
     * Protocol section
     */
    data object TransportProtocol : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_transport_protocol

        override val availableValues = listOf(
            "tcp",
            "kcp",
            "ws",
            "httpupgrade",
            "xhttp",
            "splithttp",
            "h2",
            "grpc"
        )

        override val initialValue = availableValues.first()

        override val key: String = "transport-protocol"
        override val queryKey: String = "type"

        override val subfieldsByValue: Map<String?, List<ProfileField>> = mapOf(
            "tcp" to listOf(TcpHeaderType, TcpHost, TcpPath),
            "kcp" to listOf(KcpHeaderType, KcpHost, KcpSeed),
            "ws" to listOf(WSHost, WSPath),
            "httpupgrade" to listOf(HTTPUpgradeHost, HTTPUpgradePath),
            "xhttp" to listOf(XHTTPMode, XHTTPHost, XHTTPPath, XHTTPJsonExtra),
            "splithttp" to listOf(XHTTPMode, XHTTPHost, XHTTPPath, XHTTPJsonExtra),
            "h2" to listOf(H2Host, H2Path),
            "grpc" to listOf(GRPCMode, GRPCAuthority, GRPCService),
        )
    }

    /** TCP */
    data object TcpHeaderType : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_header_type
        override val availableValues = listOf(
            "none",
            "http",
        )
        override val initialValue = availableValues.first()
        override val key: String = "tcp-header-type"
        override val queryKey: String = "headerType"
    }

    data object TcpHost : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_http_host
        override val key: String = "http-host"
        override val queryKey: String = "host"
    }

    data object TcpPath : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_path
        override val key: String = "http-path"
        override val queryKey: String = "path"
    }

    /** KCP */
    data object KcpHeaderType : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_header_type
        override val availableValues = listOf(
            "none",
            "srtp",
            "utp",
            "wechat-video",
            "dtls",
            "wireguard"
        )
        override val initialValue = availableValues.first()
        override val key: String = "kcp-header-type"
        override val queryKey: String = "headerType"
    }

    data object KcpHost : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_host
        override val key: String = "kcp-host"
        override val queryKey: String = "host"
    }

    data object KcpSeed : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_kcp_seed
        override val key: String = "kcp-seed"
        override val queryKey: String = "seed"
    }

    /** WS */
    data object WSHost : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_ws_host
        override val key: String = "ws-host"
        override val queryKey: String = "host"
    }

    data object WSPath : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_ws_path
        override val key: String = "ws-path"
        override val queryKey: String = "path"
    }

    /** httpupgrade */
    data object HTTPUpgradeHost : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_httpupgrade_host
        override val key: String = "httpupgrade-host"
        override val queryKey: String = "host"
    }

    data object HTTPUpgradePath : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_httpupgrade_path
        override val key: String = "httpupgrade-path"
        override val queryKey: String = "path"
    }

    /** xhttp */
    data object XHTTPMode : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_xhttp_mode
        override val availableValues = listOf(
            "auto",
            "packet-up",
            "stream-up",
        )
        override val initialValue = availableValues.first()
        override val key: String = "xhttp-mode"
        override val queryKey: String = "mode"
    }

    data object XHTTPHost : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_xhttp_host
        override val key: String = "xhttp-host"
        override val queryKey: String = "host"
    }

    data object XHTTPPath : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_xhttp_path
        override val key: String = "xhttp-path"
        override val queryKey: String = "path"
    }

    data object XHTTPJsonExtra : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_xhttp_json_extra
        override val key: String = "xhttp-json-extra"
        override val queryKey: String = "extra"
        override val multiline: Boolean = true
    }

    /** H2 */
    data object H2Host : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_h2_host
        override val key: String = "h2-host"
        override val queryKey: String = "host"
    }

    data object H2Path : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_h2_path
        override val key: String = "h2-path"
        override val queryKey: String = "path"
    }

    /** gRPC */
    data object GRPCMode : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_grpc_mode
        override val availableValues = listOf(
            "gun",
            "multi",
        )
        override val initialValue = availableValues.first()
        override val key: String = "gRPC-mode"
        override val queryKey: String = "mode"
    }

    data object GRPCAuthority : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_grpc_authority
        override val key: String = "gRPC-authority"
        override val queryKey: String = "authority"
    }

    data object GRPCService : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_grpc_service
        override val key: String = "gRPC-service"
        override val queryKey: String = "serviceName"
    }

    /**
     * TLS section
     */
    data object TLS : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_tls

        override val availableValues = listOf(
            null,
            "tls",
            "reality",
        )

        override val initialValue = availableValues.first()

        override val subfieldsByValue: Map<String?, List<ProfileField>> = mapOf(
            "tls" to listOf(SNI, Fingerprint, ALPN, AllowInsecure),
            "reality" to listOf(SNI, Fingerprint, PublicKey, ShortID, SpiderX),
        )

        override val key: String = "tls"
        override val queryKey: String = "security"
    }

    data object SNI : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_sni
        override val key: String = "sni"
        override val queryKey: String = "sni"
    }

    data object Fingerprint : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_fingerprint

        override val availableValues = listOf(
            null,
            "chrome",
            "firefox",
            "safari",
            "ios",
            "android",
            "edge",
            "360",
            "qq",
            "random"
        )

        override val key: String = "fingerprint"
        override val queryKey: String = "fp"
    }

    data object PublicKey : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_public_key
        override val key: String = "public-key"
        override val queryKey: String = "pbk"
    }

    data object ShortID : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_short_id
        override val key: String = "short-id"
        override val queryKey: String = "sid"
    }

    data object SpiderX : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_spider_x
        override val key: String = "spider-x"
        override val queryKey: String = "spx"
    }

    data object ALPN : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_alpn

        override val availableValues = listOf(
            null,
            "h3",
            "h2",
            "http/1.1",
            "h3,h2,http/1.1",
            "h3,h2",
            "h2,http/1.1"
        )

        override val initialValue = availableValues.first()

        override val key: String = "alpn"
        override val queryKey: String = "alpn"
    }

    data object AllowInsecure : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_allow_insecure

        override val availableValues = listOf(
            "false",
            "true",
        )

        override val initialValue = availableValues.first()

        override val key: String = "allow-insecure"
    }
}
