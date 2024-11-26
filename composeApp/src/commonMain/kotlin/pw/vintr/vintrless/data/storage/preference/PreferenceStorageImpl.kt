package pw.vintr.vintrless.data.storage.preference

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class)
class PreferenceStorageImpl(
    private val settings: FlowSettings,
) : PreferenceStorage {

    override suspend fun saveString(key: String, value: String) {
        settings.putString(key, value)
    }

    override suspend fun getString(key: String): String? {
        return settings.getStringOrNull(key)
    }

    override fun getStringFlow(key: String): Flow<String?> {
        return settings.getStringOrNullFlow(key)
    }

    override suspend fun remove(key: String) {
        settings.remove(key)
    }
}
