package pw.vintr.vintrless.data.profile.source

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.copyFromRealm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pw.vintr.vintrless.data.profile.model.ProfileDataCacheObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileCacheDataSource(private val realm: Realm) {

    val profileDataFlow = realm.query<ProfileDataCacheObject>()
        .asFlow()
        .map { it.list.copyFromRealm() }

    suspend fun saveProfile(profileData: ProfileDataCacheObject) {
        realm.write {
            val currentProfile = query<ProfileDataCacheObject>("id = $0", profileData.id)
                .first()
                .find()

            currentProfile?.let {
                // Update existing profile
                it.typeCode = profileData.typeCode
                it.data = profileData.data
            } ?: run {
                // Save new profile
                copyToRealm(profileData)
            }
        }
    }

    suspend fun getProfile(id: String): ProfileDataCacheObject? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val profileData = realm.query<ProfileDataCacheObject>("id = $0", id)
                    .first()
                    .find()
                    ?.copyFromRealm()

                continuation.resume(profileData)
            }
        }
    }

    suspend fun removeProfile(id: String) {
        realm.write {
            query<ProfileDataCacheObject>("id = $0", id)
                .first()
                .find()
                ?.let { delete(it) }
        }
    }
}
