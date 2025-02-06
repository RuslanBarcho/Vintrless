package pw.vintr.vintrless.v2ray.storage

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.v2ray.model.V2RayEncodedConfig
import pw.vintr.vintrless.tools.MultiprocessPreferences
import pw.vintr.vintrless.tools.extensions.Empty

/**
 * A local Android-only config storage, designed to store last used App filter config.
 * Should not be used for another data.
 */
object AppFilterConfigStorage {

    private const val KEY_CONFIG = "app_filter_config"

    fun saveConfig(context: Context, config: ApplicationFilterConfig) {
        val settings = getSettings(context)
        settings.edit().putString(KEY_CONFIG, Json.encodeToString(config)).commit()
    }

    fun getConfig(context: Context): ApplicationFilterConfig? {
        val encodedConfig: String = getSettings(context).getString(KEY_CONFIG, String.Empty)

        return if (encodedConfig.isNotEmpty()) {
            Json.decodeFromString<ApplicationFilterConfig>(encodedConfig)
        } else {
            null
        }
    }

    private fun getSettings(context: Context): MultiprocessPreferences.MultiprocessSharedPreferences {
        return MultiprocessPreferences.getDefaultSharedPreferences(context)
    }
}
