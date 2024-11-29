package pw.vintr.vintrless.tools.network

import pw.vintr.vintrless.tools.extensions.Empty

object IPTools {

    fun isIpv6Address(value: String): Boolean {
        var addr = value
        if (addr.indexOf("[") == 0 && addr.lastIndexOf("]") > 0) {
            addr = addr.drop(1)
            addr = addr.dropLast(addr.count() - addr.lastIndexOf("]"))
        }
        val regV6 =
            Regex("^((?:[0-9A-Fa-f]{1,4}))?((?::[0-9A-Fa-f]{1,4}))*::((?:[0-9A-Fa-f]{1,4}))?((?::[0-9A-Fa-f]{1,4}))*|((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4})){7}$")
        return regV6.matches(addr)
    }

    fun getIpv6Address(address: String?): String {
        if (address == null) {
            return String.Empty
        }
        return if (
            isIpv6Address(address) &&
            !address.contains('[') &&
            !address.contains(']')
        ) { "[$address]" } else { address }
    }
}
