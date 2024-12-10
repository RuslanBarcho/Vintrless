package pw.vintr.vintrless.domain.v2ray.useCase

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.routing.model.Ruleset
import pw.vintr.vintrless.domain.v2ray.V2RayConfigDefaults
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig
import pw.vintr.vintrless.domain.v2ray.useCase.outbounds.VlessOutboundBuildUseCase
import pw.vintr.vintrless.domain.v2ray.useCase.routing.V2RayRoutingBuildUseCase

object V2RayConfigBuildUseCase {

    operator fun invoke(profile: ProfileData, ruleset: Ruleset = Ruleset.Global): V2RayEncodedConfig {
        val address = profile.getField(ProfileField.IP)
        val port = profile.getField(ProfileField.Port)

        val config = V2RayConfig(
            remarks = profile.name,
            inbounds = getInbounds(),
            log = V2RayConfig.LogBean(
                loglevel = "warning"
            ),
            outbounds = getOutbounds(profile),
            dns = getDns(),
            routing = V2RayRoutingBuildUseCase(ruleset),
        )

        return V2RayEncodedConfig(
            id = profile.id,
            name = profile.name,
            domainName = "$address:$port",
            configJson = config.toJson()
        )
    }

    private fun getInbounds(): ArrayList<V2RayConfig.InboundBean> {
        val socksPort = 10808
        val httpPort = 10809

        val socketInbounds = V2RayConfig.InboundBean(
            listen = V2RayConfigDefaults.DEFAULT_LOOPBACK,
            port = socksPort,
            protocol = "socks",
            settings = V2RayConfig.InboundBean.InSettingsBean(
                auth = "noauth",
                udp = true,
                userLevel = 8,
            ),
            sniffing = V2RayConfig.InboundBean.SniffingBean(
                destOverride = arrayListOf(
                    "http",
                    "tls"
                ),
                enabled = true,
                routeOnly = false,
            ),
            tag = "socks",
        )
        val httpInbounds = V2RayConfig.InboundBean(
            listen = V2RayConfigDefaults.DEFAULT_LOOPBACK,
            port = httpPort,
            protocol = "http",
            settings = V2RayConfig.InboundBean.InSettingsBean(
                userLevel = 8
            ),
            sniffing = null,
            tag = "http",
        )

        return arrayListOf(socketInbounds, httpInbounds)
    }

    private fun getOutbounds(profile: ProfileData): ArrayList<V2RayConfig.OutboundBean> {
        val proxy = when (profile.type) {
            ProtocolType.VLESS -> VlessOutboundBuildUseCase(profile)
            ProtocolType.VMESS,
            ProtocolType.SHADOWSOCKS,
            ProtocolType.SOCKS,
            ProtocolType.TROJAN,
            ProtocolType.WIREGUARD,
            ProtocolType.HYSTERIA2,
            ProtocolType.HTTP -> V2RayConfig.OutboundBean(protocol = "stub")
        }

        val direct = V2RayConfig.OutboundBean(
            protocol = "freedom",
            settings = V2RayConfig.OutboundBean.OutSettingsBean(
                domainStrategy = "UseIP"
            ),
            tag = "direct"
        )
        val block = V2RayConfig.OutboundBean(
            protocol = "blackhole",
            settings = V2RayConfig.OutboundBean.OutSettingsBean(
                response = V2RayConfig.OutboundBean.OutSettingsBean.Response(
                    type = "http"
                )
            ),
            tag = "block"
        )

        return arrayListOf(proxy, direct, block)
    }

    private fun getDns(): V2RayConfig.DnsBean {
        val hosts = mutableMapOf<String, Any>()

        // hardcode googleapi rule to fix play store problems
        hosts["geosite:category-ads-all"] = V2RayConfigDefaults.DEFAULT_LOOPBACK
        hosts[V2RayConfigDefaults.GOOGLEAPIS_CN_DOMAIN] = V2RayConfigDefaults.GOOGLEAPIS_COM_DOMAIN

        // hardcode popular Android Private DNS rule to fix localhost DNS problem
        hosts[V2RayConfigDefaults.DNS_ALIDNS_DOMAIN] = V2RayConfigDefaults.DNS_ALIDNS_ADDRESSES
        hosts[V2RayConfigDefaults.DNS_CLOUDFLARE_DOMAIN] = V2RayConfigDefaults.DNS_CLOUDFLARE_ADDRESSES
        hosts[V2RayConfigDefaults.DNS_DNSPOD_DOMAIN] = V2RayConfigDefaults.DNS_DNSPOD_ADDRESSES
        hosts[V2RayConfigDefaults.DNS_GOOGLE_DOMAIN] = V2RayConfigDefaults.DNS_GOOGLE_ADDRESSES
        hosts[V2RayConfigDefaults.DNS_QUAD9_DOMAIN] = V2RayConfigDefaults.DNS_QUAD9_ADDRESSES
        hosts[V2RayConfigDefaults.DNS_YANDEX_DOMAIN] = V2RayConfigDefaults.DNS_YANDEX_ADDRESSES

        // Servers
        val servers = ArrayList<Any?>()

        servers.add("1.1.1.1")
        servers.add(
            V2RayConfig.DnsBean.ServersBean(
                address = "1.1.1.1",
                domains = listOf(
                    "domain:googleapis.cn",
                    "domain:gstatic.com"
                ),
            )
        )
        servers.add(
            V2RayConfig.DnsBean.ServersBean(
                address = "223.5.5.5",
                domains = listOf(
                    "domain:dns.alidns.com",
                    "domain:doh.pub",
                    "domain:dot.pub",
                    "domain:doh.360.cn",
                    "domain:dot.360.cn",
                    "geosite:cn",
                    "geosite:geolocation-cn"
                ),
                expectIPs = listOf(
                    "geoip:cn"
                ),
                skipFallback = true
            )
        )

        return V2RayConfig.DnsBean(
            hosts = hosts,
            servers = servers,
        )
    }
}
