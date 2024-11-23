package pw.vintr.vintrless.data.profile.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pw.vintr.vintrless.data.profile.model.ProfileDataStorageObject
import pw.vintr.vintrless.data.storage.Storage

class ProfileRepository(
    private val storage: Storage,
) {
    companion object {
        private const val PROFILE_COLLECTION_KEY = "profile_collection"
        private const val PROFILE_SELECTION_KEY = "profile_selection"
    }

    val profileFlow: Flow<List<ProfileDataStorageObject>> = storage
        .getCollectionFlow(PROFILE_COLLECTION_KEY)

    val selectedProfileFlow: Flow<ProfileDataStorageObject?> = storage
        .getStringFlow(PROFILE_SELECTION_KEY)
        .map {
            if (it != null) { getProfile(it) } else { null }
        }

    suspend fun saveProfile(profile: ProfileDataStorageObject) {
        storage.saveToCollection(PROFILE_COLLECTION_KEY, profile)
    }

    suspend fun getProfile(id: String): ProfileDataStorageObject? {
        return storage.getFromCollection(PROFILE_COLLECTION_KEY, id)
    }

    suspend fun setSelectedProfile(id: String) {
        storage.saveString(PROFILE_SELECTION_KEY, id)
    }

    suspend fun getSelectedProfile(): ProfileDataStorageObject? {
        val selectedId = storage.getString(PROFILE_SELECTION_KEY)
        return if (selectedId != null) { getProfile(selectedId) } else { null }
    }
}
