package pw.vintr.vintrless.data.storage.preference

import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {

    suspend fun saveString(key: String, value: String)

    suspend fun getString(key: String): String?

    fun getStringFlow(key: String): Flow<String?>

    suspend fun remove(key: String)
}
