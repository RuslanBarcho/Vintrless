package pw.vintr.vintrless.domain.singbox.useCase

import pw.vintr.vintrless.domain.singbox.model.Route
import pw.vintr.vintrless.domain.singbox.model.RouteRule
import pw.vintr.vintrless.domain.singbox.model.RuleSet
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig

object SingBoxRouteBuildUseCase {

    private val defaultDirectProcesses = listOf(
        "wv2ray.exe",
        "v2ray.exe",
        "SagerNet.exe",
        "xray.exe",
        "wxray.exe",
        "clash-windows-amd64-v3.exe",
        "clash-windows-amd64.exe",
        "clash-windows-386.exe",
        "clash.exe",
        "Clash.Meta-windows-amd64-compatible.exe",
        "Clash.Meta-windows-amd64.exe",
        "Clash.Meta-windows-386.exe",
        "Clash.Meta.exe",
        "mihomo-windows-amd64.exe",
        "mihomo-windows-amd64-compatible.exe",
        "mihomo-windows-386.exe", "mihomo.exe",
        "hysteria-windows-amd64.exe",
        "hysteria-windows-386.exe",
        "hysteria.exe",
        "naiveproxy.exe",
        "naive.exe",
        "tuic-client.exe",
        "tuic.exe",
        "sing-box-client.exe",
        "sing-box.exe",
        "juicity-client.exe",
        "juicity.exe"
    )

    private val defaultDirectIps = listOf(
        "223.5.5.5/32",
        "223.6.6.6/32",
        "2400:3200::1/128",
        "2400:3200:baba::1/128",
        "119.29.29.29/32",
        "1.12.12.12/32",
        "120.53.53.53/32",
        "2402:4e00::/128",
        "2402:4e00:1::/128",
        "180.76.76.76/32",
        "2400:da00::6666/128",
        "114.114.114.114/32",
        "114.114.115.115/32",
        "180.184.1.1/32",
        "180.184.2.2/32",
        "101.226.4.6/32",
        "218.30.118.6/32",
        "123.125.81.6/32",
        "140.207.198.6/32"
    )

    operator fun invoke(appFilterConfig: ApplicationFilterConfig): Route {
        val rules = mutableListOf(
            RouteRule(
                outbound = "proxy",
                clashMode = "Global"
            ),
            RouteRule(
                outbound = "direct",
                clashMode = "Direct"
            ),
            RouteRule(
                outbound = "dns_out",
                protocol = listOf("dns")
            ),
            RouteRule(
                outbound = "dns_out",
                port = listOf(53),
                processName = defaultDirectProcesses
            ),
            RouteRule(
                outbound = "direct",
                processName = defaultDirectProcesses
            ),
            RouteRule(
                outbound = "direct",
                domain = listOf("example-example.com", "example-example2.com"),
                domainSuffix = listOf(".example-example.com", ".example-example2.com")
            ),
            RouteRule(
                outbound = "block",
                network = listOf("udp"),
                port = listOf(443)
            ),
            RouteRule(
                outbound = "block",
                ruleSet = listOf("geosite-category-ads-all")
            ),
            RouteRule(
                outbound = "direct",
                domain = listOf("dns.alidns.com", "doh.pub", "dot.pub", "doh.360.cn", "dot.360.cn"),
                domainSuffix = listOf(".dns.alidns.com", ".doh.pub", ".dot.pub", ".doh.360.cn", ".dot.360.cn"),
                ruleSet = listOf("geosite-cn", "geosite-geolocation-cn")
            ),
            RouteRule(
                outbound = "direct",
                ipIsPrivate = true,
                ipCidr = defaultDirectIps,
                ruleSet = listOf("geoip-cn")
            ),
        )

        when {
            // White list
            appFilterConfig.enabled && appFilterConfig.isExclude -> {
                rules.add(
                    RouteRule(
                        outbound = "direct",
                        processName = appFilterConfig.keys.toList()
                    )
                )
                rules.add(
                    RouteRule(
                        outbound = "proxy",
                        portRange = listOf("0:65535")
                    )
                )
            }
            // Black list
            appFilterConfig.enabled -> {
                rules.add(
                    RouteRule(
                        outbound = "proxy",
                        processName = appFilterConfig.keys.toList()
                    )
                )
                rules.add(
                    RouteRule(
                        outbound = "direct",
                        portRange = listOf("0:65535")
                    )
                )
            }
            // Filter disabled
            else -> {
                rules.add(
                    RouteRule(
                        outbound = "proxy",
                        portRange = listOf("0:65535")
                    )
                )
            }
        }

        return Route(
            autoDetectInterface = true,
            rules = rules,
            ruleSet = listOf(
                RuleSet(
                    tag = "geosite-category-ads-all",
                    type = "local",
                    format = "binary",
                    path = "srss\\geosite-category-ads-all.srs"
                ),
                RuleSet(
                    tag = "geosite-cn",
                    type = "local",
                    format = "binary",
                    path = "srss\\geosite-cn.srs"
                ),
                RuleSet(
                    tag = "geosite-geolocation-cn",
                    type = "local",
                    format = "binary",
                    path = "srss\\geosite-geolocation-cn.srs"
                ),
                RuleSet(
                    tag = "geoip-cn",
                    type = "local",
                    format = "binary",
                    path = "srss\\geoip-cn.srs"
                )
            )
        )
    }
}
