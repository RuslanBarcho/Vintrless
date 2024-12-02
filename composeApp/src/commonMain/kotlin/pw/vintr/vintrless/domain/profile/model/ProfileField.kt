package pw.vintr.vintrless.domain.profile.model

import org.jetbrains.compose.resources.StringResource
import vintrless.composeapp.generated.resources.*

sealed class ProfileField {

    open val availableValues: List<String?>? = null

    open val initialValue: String? = null

    open val subfieldsByValue: Map<String?, List<ProfileField>> = mapOf()

    open val multiline: Boolean = false

    abstract val titleRes: StringResource

    abstract val key: String

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

    data object Flow : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_flow

        override val availableValues = listOf(
            null,
            "xtls-rprx-vision",
            "xtls-rprx-vision-udp443"
        )

        override val key: String = "flow"
    }

    data object Encryption : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_encryption
        override val initialValue = "none"
        override val key: String = "encryption"
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
    }

    data object TcpHost : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_http_host
        override val key: String = "http-host"
    }

    data object TcpPath : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_path
        override val key: String = "http-path"
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
    }

    data object KcpHost : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_host
        override val key: String = "kcp-host"
    }

    data object KcpSeed : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_kcp_seed
        override val key: String = "kcp-seed"
    }

    /** WS */
    data object WSHost : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_ws_host
        override val key: String = "ws-host"
    }

    data object WSPath : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_ws_path
        override val key: String = "ws-path"
    }

    /** httpupgrade */
    data object HTTPUpgradeHost : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_httpupgrade_host
        override val key: String = "httpupgrade-host"
    }

    data object HTTPUpgradePath : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_httpupgrade_path
        override val key: String = "httpupgrade-path"
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
    }

    data object XHTTPHost : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_xhttp_host
        override val key: String = "xhttp-host"
    }

    data object XHTTPPath : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_xhttp_path
        override val key: String = "xhttp-path"
    }

    data object XHTTPJsonExtra : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_xhttp_json_extra
        override val key: String = "xhttp-json-extra"
        override val multiline: Boolean = true
    }

    /** H2 */
    data object H2Host : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_h2_host
        override val key: String = "h2-host"
    }

    data object H2Path : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_h2_path
        override val key: String = "h2-path"
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
    }

    data object GRPCAuthority : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_grpc_authority
        override val key: String = "gRPC-authority"
    }

    data object GRPCService : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_grpc_service
        override val key: String = "gRPC-service"
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
    }

    data object SNI : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_sni
        override val key: String = "sni"
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
    }

    data object PublicKey : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_public_key
        override val key: String = "public-key"
    }

    data object ShortID : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_short_id
        override val key: String = "short-id"
    }

    data object SpiderX : ProfileField() {
        override val titleRes: StringResource = Res.string.profile_field_spider_x
        override val key: String = "spider-x"
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
