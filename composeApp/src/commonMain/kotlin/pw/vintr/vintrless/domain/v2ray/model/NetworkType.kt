package pw.vintr.vintrless.domain.v2ray.model

enum class NetworkType(val type: String) {
    TCP("tcp"),
    KCP("kcp"),
    WS("ws"),
    HTTP_UPGRADE("httpupgrade"),
    SPLIT_HTTP("splithttp"),
    XHTTP("xhttp"),
    HTTP("http"),
    H2("h2"),
    GRPC("grpc");

    companion object {
        fun fromString(type: String?) = entries.find { it.type == type } ?: TCP
    }
}
