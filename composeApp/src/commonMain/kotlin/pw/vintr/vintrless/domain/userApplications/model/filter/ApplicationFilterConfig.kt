package pw.vintr.vintrless.domain.userApplications.model.filter

import kotlinx.serialization.Serializable

/**
 * App filter serializable config class
 *
 * @param enabled - use or ignore filter
 * @param isBypass - false means blacklist (only selected keys enabled), true means whitelist (only selected keys disabled)
 * @param keys - process names or applications package names
 */
@Serializable
data class ApplicationFilterConfig(
    val enabled: Boolean,
    val isBypass: Boolean,
    val keys: Set<String>,
) {

    companion object {
        fun empty() = ApplicationFilterConfig(
            enabled = false,
            isBypass = false,
            keys = setOf(),
        )
    }
}
