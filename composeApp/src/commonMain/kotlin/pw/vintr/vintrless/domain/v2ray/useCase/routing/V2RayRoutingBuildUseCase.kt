package pw.vintr.vintrless.domain.v2ray.useCase.routing

import pw.vintr.vintrless.domain.routing.model.Ruleset
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig

object V2RayRoutingBuildUseCase {

    private val commonRules = listOf(
        V2RayConfig.RoutingBean.RulesBean(
            ip = arrayListOf("1.1.1.1"),
            outboundTag = "proxy",
            port = "53",
            type = "field"
        ),
        V2RayConfig.RoutingBean.RulesBean(
            ip = arrayListOf("223.5.5.5"),
            outboundTag = "direct",
            port = "53",
            type = "field"
        ),
        V2RayConfig.RoutingBean.RulesBean(
            domain = arrayListOf(
                "domain:googleapis.cn",
                "domain:gstatic.com"
            ),
            outboundTag = "proxy",
            type = "field"
        ),
        V2RayConfig.RoutingBean.RulesBean(
            network = "udp",
            outboundTag = "block",
            port = "443",
            type = "field"
        ),
        V2RayConfig.RoutingBean.RulesBean(
            domain = arrayListOf(
                "geosite:category-ads-all"
            ),
            outboundTag = "block",
            type = "field"
        ),
        V2RayConfig.RoutingBean.RulesBean(
            ip = arrayListOf("geoip:private"),
            outboundTag = "direct",
            type = "field"
        ),
        V2RayConfig.RoutingBean.RulesBean(
            domain = arrayListOf("geosite:private"),
            outboundTag = "direct",
            type = "field"
        ),
    )

    operator fun invoke(ruleset: Ruleset): V2RayConfig.RoutingBean {
        return V2RayConfig.RoutingBean(
            domainStrategy = "AsIs",
            rules = when (ruleset) {
                is Ruleset.Exclude -> getExcludeRules(ruleset)
                else -> getGlobalProxyRules()
            }
        )
    }

    private fun getGlobalProxyRules() = arrayListOf(
        *commonRules.toTypedArray(),
        V2RayConfig.RoutingBean.RulesBean(
            domain = arrayListOf(
                "domain:dns.alidns.com",
                "domain:doh.pub",
                "domain:dot.pub",
                "domain:doh.360.cn",
                "domain:dot.360.cn",
                "geosite:cn",
                "geosite:geolocation-cn"
            ),
            outboundTag = "direct",
            type = "field"
        ),
        V2RayConfig.RoutingBean.RulesBean(
            domain = arrayListOf(
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
                "140.207.198.6/32",
                "geoip:cn"
            ),
            outboundTag = "direct",
            type = "field",
        ),
        V2RayConfig.RoutingBean.RulesBean(
            outboundTag = "proxy",
            port = "0-65535",
            type = "field"
        ),
    )

    private fun getExcludeRules(excludeRuleset: Ruleset.Exclude) = arrayListOf(
        *commonRules.toTypedArray(),
        V2RayConfig.RoutingBean.RulesBean(
            domain = ArrayList(excludeRuleset.domains),
            outboundTag = if (excludeRuleset.type == Ruleset.Exclude.Type.BLACKLIST) "proxy" else "direct",
            type = "field"
        ),
        V2RayConfig.RoutingBean.RulesBean(
            ip = ArrayList(excludeRuleset.ips),
            outboundTag = if (excludeRuleset.type == Ruleset.Exclude.Type.BLACKLIST) "proxy" else "direct",
            type = "field",
        ),
        V2RayConfig.RoutingBean.RulesBean(
            outboundTag = if (excludeRuleset.type == Ruleset.Exclude.Type.BLACKLIST) "direct" else "proxy",
            port = "0-65535",
            type = "field"
        ),
    )
}
