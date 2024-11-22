package pw.vintr.vintrless.data.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.SuspendSettings
import com.russhwolf.settings.coroutines.toBlockingSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSettingsApi::class)
class StorageImpl(
    private val settings: FlowSettings,
) : Storage {

    @OptIn(ExperimentalSettingsApi::class)
    override suspend fun <T: StorageObject> saveToCollection(key: String, value: T) {
        val currentCollection = getCollection<T>(key)

        currentCollection
            .toMutableList()
            .also {
                it.removeAll { obj -> obj.id == value.id }
                it.add(value)
            }
            .associateBy { it.id }

        settings.putString(key, Json.encodeToString(currentCollection))
    }

    override suspend fun <T: StorageObject> getCollection(key: String): List<T> {
        val json: String? = settings.getStringOrNull(key)
        return json.mapToCollection<T>().values.toList()
    }

    override suspend fun <T : StorageObject> getFromCollection(key: String, id: String): T? {
        return getCollection<T>(key).find { it.id == id }
    }

    override fun <T: StorageObject> getCollectionFlow(key: String): Flow<List<T>> {
        return settings
            .getStringOrNullFlow(key)
            .filterNotNull()
            .map { it.mapToCollection<T>().values.toList() }
    }

    override suspend fun remove(key: String) {
        settings.remove(key)
    }

    private fun <T> String?.mapToCollection(): Map<String, T> {
        return this?.let { Json.decodeFromString(it) } ?: mapOf()
    }
}
