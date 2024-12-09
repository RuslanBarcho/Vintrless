package pw.vintr.vintrless.domain.routing.model

import io.realm.kotlin.ext.toRealmList
import pw.vintr.vintrless.data.routing.model.ExcludeRulesetCacheObject

sealed class Ruleset {

    abstract val id: String

    abstract val isEmpty: Boolean

    /**
     * Default ruleset type. Means "All requests go to proxy"
     */
    data object Global : Ruleset() {
        override val id: String = "default_global"

        override val isEmpty: Boolean = false
    }

    /**
     * Default ruleset type. Means "Excluded requests (black or white listed) go to proxy"
     */
    data class Exclude(
        val type: Type,
        val ips: List<String>,
        val domains: List<String>
    ) : Ruleset() {

        enum class Type {
            BLACKLIST,
            WHITELIST;

            val id: String get() = when (this) {
                BLACKLIST -> "default_blacklist"
                WHITELIST -> "default_whitelist"
            }
        }

        override val id: String = type.id

        override val isEmpty: Boolean = ips.isEmpty() && domains.isEmpty()

        fun toCacheObject() = ExcludeRulesetCacheObject(
            id = id,
            ips = ips.toRealmList(),
            domains = domains.toRealmList(),
        )
    }

    /**
     * User's custom ruleset type. Has manually entered fields
     */
    data class Custom(
        override val id: String,
        val name: String,
        val description: String,
    ) : Ruleset() {

        override val isEmpty: Boolean = false
    }
}
