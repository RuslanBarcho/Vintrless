package pw.vintr.vintrless.domain.v2ray.useCase

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.V2RayConfigDefaults
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.domain.v2ray.model.V2RayConfig
import pw.vintr.vintrless.domain.v2ray.useCase.outbounds.VlessOutboundBuildUseCase

object V2RayConfigBuildUseCase {

    operator fun invoke(profile: ProfileData): V2RayEncodedConfig {
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
            routing = getRouting(),
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

    private fun getRouting(): V2RayConfig.RoutingBean {
        return V2RayConfig.RoutingBean(
            domainStrategy = "AsIs",
            rules = arrayListOf(
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
                    type = "field"
                ),
                V2RayConfig.RoutingBean.RulesBean(
                    outboundTag = "proxy",
                    port = "0-65535",
                    type = "field"
                ),
            )
        )
    }
}
