package pw.vintr.vintrless.domain.userApplications.model.filter

import io.realm.kotlin.ext.toRealmList
import pw.vintr.vintrless.data.userApplications.model.ApplicationFilterCacheObject

data class ApplicationFilter(
    val mode: ApplicationFilterMode,
    val filterKeys: Set<String>
) {
    companion object {
        fun fromCacheObject(cacheObject: ApplicationFilterCacheObject) = ApplicationFilter(
            mode = ApplicationFilterMode.entries.find { it.id == cacheObject.id } ?: ApplicationFilterMode.BLACKLIST,
            filterKeys = cacheObject.filterKeys.toSet(),
        )

        fun default() = ApplicationFilter(
            mode = ApplicationFilterMode.BLACKLIST,
            filterKeys = setOf(),
        )
    }

    fun toCacheObject() = ApplicationFilterCacheObject(
        id = mode.id,
        filterKeys = filterKeys.toRealmList(),
    )
}
