package pw.vintr.vintrless.data.profile.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import pw.vintr.vintrless.data.profile.model.ProfileDataCacheObject
import pw.vintr.vintrless.data.profile.source.ProfileCacheDataSource
import pw.vintr.vintrless.data.storage.preference.PreferenceStorage

class ProfileRepository(
    private val preferenceCacheDataSource: ProfileCacheDataSource,
    private val preferenceStorage: PreferenceStorage,
) {
    companion object {
        private const val PROFILE_SELECTION_KEY = "profile_selection"
    }

    val profileFlow: Flow<List<ProfileDataCacheObject>> = preferenceCacheDataSource
        .profileDataFlow

    val selectedProfileFlow: Flow<ProfileDataCacheObject?> = combine(
        preferenceStorage.getStringFlow(PROFILE_SELECTION_KEY),
        profileFlow
    ) { selectionId, profiles ->
        if (selectionId != null && profiles.isNotEmpty()) {
            getProfile(selectionId)
        } else {
            null
        }
    }

    suspend fun saveProfile(profile: ProfileDataCacheObject) {
        preferenceCacheDataSource.saveProfile(profile)
    }

    suspend fun getProfile(id: String): ProfileDataCacheObject? {
        return preferenceCacheDataSource.getProfile(id)
    }

    suspend fun setSelectedProfile(id: String) {
        preferenceStorage.saveString(PROFILE_SELECTION_KEY, id)
    }

    suspend fun getSelectedProfile(): ProfileDataCacheObject? {
        val selectedId = preferenceStorage.getString(PROFILE_SELECTION_KEY)
        return if (selectedId != null) { getProfile(selectedId) } else { null }
    }

    suspend fun removeProfile(id: String) {
        preferenceCacheDataSource.removeProfile(id)
    }
}
