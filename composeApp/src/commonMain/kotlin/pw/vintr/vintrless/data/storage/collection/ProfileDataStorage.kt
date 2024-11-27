package pw.vintr.vintrless.data.storage.collection

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pw.vintr.vintrless.data.profile.model.ProfileDataStorageObject

@OptIn(ExperimentalSettingsApi::class)
class ProfileDataStorage(
    private val settings: FlowSettings,
) : CollectionStorage<ProfileDataStorageObject> {

    @OptIn(ExperimentalSettingsApi::class)
    override suspend fun saveToCollection(key: String, value: ProfileDataStorageObject) {
        val currentCollection = getCollection(key)

        val modifiedCollection = currentCollection
            .toMutableList()
            .also {
                it.removeAll { obj -> obj.id == value.id }
                it.add(value)
            }
            .associateBy { it.id }

        settings.putString(key, Json.encodeToString(modifiedCollection))
    }

    override suspend fun getCollection(key: String): List<ProfileDataStorageObject> {
        val json: String? = settings.getStringOrNull(key)
        return json.mapToCollection().values.toList()
    }

    override suspend fun getFromCollection(key: String, id: String): ProfileDataStorageObject? {
        val json: String? = settings.getStringOrNull(key)
        return json.mapToCollection()[id]
    }

    override suspend fun removeFromCollection(key: String, id: String) {
        val currentCollection = getCollection(key)

        val modifiedCollection = currentCollection
            .toMutableList()
            .also {
                it.removeAll { obj -> obj.id == id }
            }
            .associateBy { it.id }

        settings.putString(key, Json.encodeToString(modifiedCollection))
    }

    override fun getCollectionFlow(key: String): Flow<List<ProfileDataStorageObject>> {
        return settings
            .getStringOrNullFlow(key)
            .map { it?.mapToCollection()?.values?.toList().orEmpty() }
    }

    private fun String?.mapToCollection(): Map<String, ProfileDataStorageObject> {
        return this?.let { Json.decodeFromString(it) } ?: mapOf()
    }
}
