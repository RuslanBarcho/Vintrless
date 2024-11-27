package pw.vintr.vintrless.data.profile.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import pw.vintr.vintrless.data.profile.model.ProfileDataStorageObject
import pw.vintr.vintrless.data.storage.collection.CollectionStorage
import pw.vintr.vintrless.data.storage.preference.PreferenceStorage

class ProfileRepository(
    private val collectionStorage: CollectionStorage<ProfileDataStorageObject>,
    private val preferenceStorage: PreferenceStorage,
) {
    companion object {
        private const val PROFILE_COLLECTION_KEY = "profile_collection"
        private const val PROFILE_SELECTION_KEY = "profile_selection"
    }

    val profileFlow: Flow<List<ProfileDataStorageObject>> = collectionStorage
        .getCollectionFlow(PROFILE_COLLECTION_KEY)

    val selectedProfileFlow: Flow<ProfileDataStorageObject?> = combine(
        preferenceStorage.getStringFlow(PROFILE_SELECTION_KEY),
        profileFlow
    ) { selectionId, profiles ->
        if (selectionId != null && profiles.isNotEmpty()) {
            getProfile(selectionId)
        } else {
            null
        }
    }

    suspend fun saveProfile(profile: ProfileDataStorageObject) {
        collectionStorage.saveToCollection(PROFILE_COLLECTION_KEY, profile)
    }

    suspend fun getProfile(id: String): ProfileDataStorageObject? {
        return collectionStorage.getFromCollection(PROFILE_COLLECTION_KEY, id)
    }

    suspend fun setSelectedProfile(id: String) {
        preferenceStorage.saveString(PROFILE_SELECTION_KEY, id)
    }

    suspend fun getSelectedProfile(): ProfileDataStorageObject? {
        val selectedId = preferenceStorage.getString(PROFILE_SELECTION_KEY)
        return if (selectedId != null) { getProfile(selectedId) } else { null }
    }

    suspend fun removeProfile(id: String) {
        collectionStorage.removeFromCollection(PROFILE_COLLECTION_KEY, id)
    }
}
