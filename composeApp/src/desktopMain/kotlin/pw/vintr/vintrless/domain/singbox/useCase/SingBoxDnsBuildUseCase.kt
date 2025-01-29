package pw.vintr.vintrless.domain.singbox.useCase

import pw.vintr.vintrless.domain.singbox.model.Dns
import pw.vintr.vintrless.domain.singbox.model.Rule
import pw.vintr.vintrless.domain.singbox.model.Server

object SingBoxDnsBuildUseCase {

    operator fun invoke(): Dns {
        return Dns(
            servers = listOf(
                Server(
                    tag = "remote",
                    address = "8.8.8.8",
                    strategy = "ipv4_only",
                    detour = "proxy"
                ),
                Server(
                    tag = "local",
                    address = "223.5.5.5",
                    strategy = "ipv4_only",
                    detour = "direct"
                ),
                Server(
                    tag = "block",
                    address = "rcode://success"
                ),
                Server(
                    tag = "local_local",
                    address = "223.5.5.5",
                    detour = "direct"
                )
            ),
            rules = listOf(
                Rule(
                    server = "remote",
                    clashMode = "Global"
                ),
                Rule(
                    server = "local_local",
                    clashMode = "Direct"
                ),
                Rule(
                    server = "local",
                    ruleSet = listOf("geosite-cn", "geosite-geolocation-cn")
                ),
                Rule(
                    server = "block",
                    ruleSet = listOf("geosite-category-ads-all")
                )
            ),
            final = "remote"
        )
    }
}
