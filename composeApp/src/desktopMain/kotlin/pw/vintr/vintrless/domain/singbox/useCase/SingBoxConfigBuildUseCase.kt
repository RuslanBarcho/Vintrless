package pw.vintr.vintrless.domain.singbox.useCase

import pw.vintr.vintrless.domain.singbox.model.*
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig

object SingBoxConfigBuildUseCase {

    operator fun invoke(appFilterConfig: ApplicationFilterConfig): SingBoxConfig {
        return SingBoxConfig(
            log = Log(
                level = "warn",
                timestamp = true
            ),
            dns = SingBoxDnsBuildUseCase(),
            inbounds = listOf(
                Inbound(
                    type = "tun",
                    tag = "tun-in",
                    interfaceName = "singbox_tun",
                    inet4Address = "172.19.0.1/30",
                    mtu = 9000,
                    autoRoute = true,
                    strictRoute = true,
                    stack = "gvisor",
                    sniff = true
                )
            ),
            outbounds = listOf(
                Outbound(
                    type = "socks",
                    tag = "proxy",
                    server = "127.0.0.1",
                    serverPort = 10808,
                    version = "5"
                ),
                Outbound(
                    type = "direct",
                    tag = "direct"
                ),
                Outbound(
                    type = "block",
                    tag = "block"
                ),
                Outbound(
                    type = "dns",
                    tag = "dns_out"
                )
            ),
            route = SingBoxRouteBuildUseCase(appFilterConfig),
            experimental = Experimental(
                cacheFile = CacheFile(enabled = true),
                clashApi = ClashApi(externalController = "127.0.0.1:10814")
            )
        )
    }
}
