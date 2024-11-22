package pw.vintr.vintrless.data.storage

import kotlinx.coroutines.flow.Flow

interface Storage {

    suspend fun <T: StorageObject> saveToCollection(key: String, value: T)

    suspend fun <T: StorageObject> getCollection(key: String): List<T>

    suspend fun <T: StorageObject> getFromCollection(key: String, id: String): T?

    fun <T: StorageObject> getCollectionFlow(key: String): Flow<List<T>>

    suspend fun saveString(key: String, value: String)

    suspend fun getString(key: String): String?

    fun getStringFlow(key: String): Flow<String?>

    suspend fun remove(key: String)
}
