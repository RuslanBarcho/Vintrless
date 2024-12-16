package pw.vintr.vintrless.v2ray.storage

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.tools.MultiprocessPreferences
import pw.vintr.vintrless.tools.extensions.Empty

/**
 * A local Android-only config storage, designed to store last used V2Ray config with additional params.
 * Should not be used for another data or outside the V2RayServiceController
 */
object V2RayConfigStorage {

    private const val KEY_CONFIG = "config"

    fun saveConfig(context: Context, config: V2RayEncodedConfig) {
        val settings = getSettings(context)
        settings.edit().putString(KEY_CONFIG, Json.encodeToString(config)).commit()
    }

    fun getConfig(context: Context): V2RayEncodedConfig? {
        val encodedConfig: String = getSettings(context).getString(KEY_CONFIG, String.Empty)

        return if (encodedConfig.isNotEmpty()) {
            Json.decodeFromString<V2RayEncodedConfig>(encodedConfig)
        } else {
            null
        }
    }

    private fun getSettings(context: Context): MultiprocessPreferences.MultiprocessSharedPreferences {
        return MultiprocessPreferences.getDefaultSharedPreferences(context)
    }
}
