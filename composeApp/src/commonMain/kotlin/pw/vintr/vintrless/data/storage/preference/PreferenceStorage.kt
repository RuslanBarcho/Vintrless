package pw.vintr.vintrless.data.storage.preference

import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {

    suspend fun saveString(key: String, value: String)

    suspend fun getString(key: String): String?

    fun getStringFlow(key: String): Flow<String?>

    suspend fun saveBoolean(key: String, value: Boolean)

    suspend fun getBoolean(key: String): Boolean

    fun getBooleanFlow(key: String): Flow<Boolean>

    suspend fun remove(key: String)
}
