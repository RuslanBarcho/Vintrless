package pw.vintr.vintrless.data.routing.repository

import pw.vintr.vintrless.data.routing.model.ExcludeRulesetCacheObject
import pw.vintr.vintrless.data.routing.source.ExcludeRulesetCacheDataSource
import pw.vintr.vintrless.data.storage.preference.PreferenceStorage

class RoutingRepository(
    private val excludeRulesetCacheDataSource: ExcludeRulesetCacheDataSource,
    private val preferenceStorage: PreferenceStorage,
) {
    companion object {
        private const val RULESET_SELECTION_KEY = "ruleset_selection"
    }

    suspend fun setSelectedRulesetId(id: String) {
        preferenceStorage.saveString(RULESET_SELECTION_KEY, id)
    }

    suspend fun getSelectedRulesetId(): String? {
        return preferenceStorage.getString(RULESET_SELECTION_KEY)
    }

    suspend fun saveExcludeRuleset(ruleset: ExcludeRulesetCacheObject) {
        excludeRulesetCacheDataSource.saveExcludeRuleset(ruleset)
    }

    suspend fun getExcludeRuleset(id: String): ExcludeRulesetCacheObject? {
        return excludeRulesetCacheDataSource.getExcludeRuleset(id)
    }
}
