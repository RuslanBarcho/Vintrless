package pw.vintr.vintrless.data.profile.repository

import kotlinx.coroutines.flow.Flow
import pw.vintr.vintrless.data.profile.model.ProfileDataStorageObject
import pw.vintr.vintrless.data.storage.Storage

class ProfileRepository(
    private val storage: Storage,
) {
    companion object {
        private const val PROFILE_COLLECTION_KEY = "profile-collection"
    }

    val profileFlow: Flow<List<ProfileDataStorageObject>> = storage
        .getCollectionFlow(PROFILE_COLLECTION_KEY)

    suspend fun saveProfile(profile: ProfileDataStorageObject) {
        storage.saveToCollection(PROFILE_COLLECTION_KEY, profile)
    }

    suspend fun getProfile(id: String): ProfileDataStorageObject? {
        return storage.getFromCollection(PROFILE_COLLECTION_KEY, id)
    }
}
