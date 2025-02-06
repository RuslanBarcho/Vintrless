package pw.vintr.vintrless.data.userApplications.source

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.copyFromRealm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import pw.vintr.vintrless.data.profile.model.ProfileDataCacheObject
import pw.vintr.vintrless.data.userApplications.model.SystemProcessCacheObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SystemProcessCacheDataSource(private val realm: Realm) {

    suspend fun saveSystemProcess(systemProcess: SystemProcessCacheObject) {
        realm.write {
            val existingRecord = query<SystemProcessCacheObject>("id = $0", systemProcess.id)
                .first()
                .find()

            existingRecord?.let {
                // Update existing ruleset
                it.appName = systemProcess.appName
                it.processName = systemProcess.processName
            } ?: run {
                // Save new ruleset
                copyToRealm(systemProcess)
            }
        }
    }

    suspend fun removeProcess(id: String) {
        realm.write {
            query<SystemProcessCacheObject>("id = $0", id)
                .first()
                .find()
                ?.let { delete(it) }
        }
    }

    suspend fun getSavedSystemProcesses(): List<SystemProcessCacheObject> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val records = realm.query<SystemProcessCacheObject>()
                    .find()
                    .copyFromRealm()

                continuation.resume(records)
            }
        }
    }
}
