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

    override suspend fun saveBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
    }

    override suspend fun getBoolean(key: String): Boolean {
        return settings.getBoolean(key, defaultValue = false)
    }

    override fun getBooleanFlow(key: String): Flow<Boolean> {
        return settings.getBooleanFlow(key, defaultValue = false)
    }

    override suspend fun remove(key: String) {
        settings.remove(key)
    }
}
