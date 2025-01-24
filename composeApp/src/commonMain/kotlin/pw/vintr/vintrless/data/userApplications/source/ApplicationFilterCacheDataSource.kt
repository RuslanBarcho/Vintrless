package pw.vintr.vintrless.data.userApplications.source

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.copyFromRealm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import pw.vintr.vintrless.data.userApplications.model.ApplicationFilterCacheObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ApplicationFilterCacheDataSource(private val realm: Realm) {

    suspend fun saveFilter(applicationFilter: ApplicationFilterCacheObject) {
        realm.write {
            val currentFilter = query<ApplicationFilterCacheObject>()
                .first()
                .find()

            currentFilter?.let {
                // Update existing ruleset
                it.id = applicationFilter.id
                it.filterKeys = applicationFilter.filterKeys
            } ?: run {
                // Save new ruleset
                copyToRealm(applicationFilter)
            }
        }
    }

    suspend fun getFilter(): ApplicationFilterCacheObject? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val filter = realm.query<ApplicationFilterCacheObject>()
                    .first()
                    .find()
                    ?.copyFromRealm()

                continuation.resume(filter)
            }
        }
    }
}
