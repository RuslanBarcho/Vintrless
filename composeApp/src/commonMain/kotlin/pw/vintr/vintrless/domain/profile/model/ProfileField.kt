package pw.vintr.vintrless.domain.profile.model

import org.jetbrains.compose.resources.StringResource
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.profile_field_ip
import vintrless.composeapp.generated.resources.profile_field_name
import vintrless.composeapp.generated.resources.profile_field_port

sealed class ProfileField {

    open val availableValues: List<String?>? = null

    open val initialValue: String? = null

    open val subfieldsByValue: Map<String?, List<ProfileField>> = mapOf()

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

    data object TransportProtocol : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_transport_protocol

        override val availableValues = listOf(
            "tcp",
            "kcp",
            "ws",
            "httpupgrade",
            "splithttp",
            "h2",
            "quic",
            "grpc"
        )

        override val initialValue = availableValues.first()

        override val key: String = "transport-protocol"
    }

    data object HeaderType : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_header_type

        override val availableValues = listOf(
            "none",
            "http",
        )

        override val initialValue = availableValues.first()

        override val key: String = "header-type"
    }

    data object HttpNode : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_http_node

        override val key: String = "http-node"
    }

    data object Path : ProfileField() {

        override val titleRes: StringResource = Res.string.profile_field_path

        override val key: String = "path"
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
}
