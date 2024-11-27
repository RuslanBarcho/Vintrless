package pw.vintr.vintrless.domain.profile.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pw.vintr.vintrless.data.profile.repository.ProfileRepository
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData

class ProfileInteractor(
    private val repository: ProfileRepository
) : BaseInteractor() {

    val profileFlow: Flow<List<ProfileData>> = repository.profileFlow
        .map { cacheList -> cacheList.map { ProfileData.fromStorageObject(it) } }

    val selectedProfileFlow: Flow<ProfileData?> = repository.selectedProfileFlow
        .map { selectedProfile -> selectedProfile?.let { ProfileData.fromStorageObject(it) } }

    suspend fun saveProfile(profile: ProfileData) {
        repository.saveProfile(profile.toStorageObject())
    }

    suspend fun getProfile(id: String): ProfileData? {
        return repository.getProfile(id)
            ?.let { ProfileData.fromStorageObject(it) }
    }

    suspend fun setSelectedProfile(id: String) {
        repository.setSelectedProfile(id)
    }

    suspend fun getSelectedProfile(): ProfileData? {
        return repository.getSelectedProfile()
            ?.let { ProfileData.fromStorageObject(it) }
    }

    suspend fun removeProfile(id: String) {
        repository.removeProfile(id)
    }
}
