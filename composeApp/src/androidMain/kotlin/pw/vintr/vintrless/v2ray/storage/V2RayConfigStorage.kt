package pw.vintr.vintrless.v2ray.storage

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig

/**
 * A local Android-only config storage, designed to store last used V2Ray config with additional params.
 * Should not be used for another data or outside the V2RayServiceController
 */
object V2RayConfigStorage {

    private const val STORAGE_NAME = "V2RayConfigStorage"

    private const val KEY_CONFIG = "config"

    fun saveConfig(context: Context, config: V2RayEncodedConfig) {
        getSettings(context)[KEY_CONFIG] = Json.encodeToString(config)
    }

    fun getConfig(context: Context): V2RayEncodedConfig? {
        val encodedConfig: String? = getSettings(context)[KEY_CONFIG]
        return encodedConfig?.let { Json.decodeFromString(it) }
    }

    private fun getSettings(context: Context): SharedPreferencesSettings {
        return SharedPreferencesSettings
            .Factory(context.applicationContext)
            .create(name = STORAGE_NAME)
    }
}
