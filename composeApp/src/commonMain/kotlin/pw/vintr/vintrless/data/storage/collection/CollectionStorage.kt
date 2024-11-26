package pw.vintr.vintrless.data.storage.collection

import kotlinx.coroutines.flow.Flow
import pw.vintr.vintrless.data.storage.StorageObject

interface CollectionStorage<T: StorageObject> {

    suspend fun saveToCollection(key: String, value: T)

    suspend fun getCollection(key: String): List<T>

    suspend fun getFromCollection(key: String, id: String): T?

    fun getCollectionFlow(key: String): Flow<List<T>>
}
