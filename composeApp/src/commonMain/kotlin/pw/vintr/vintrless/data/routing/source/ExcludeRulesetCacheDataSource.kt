package pw.vintr.vintrless.data.routing.source

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.copyFromRealm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import pw.vintr.vintrless.data.routing.model.ExcludeRulesetCacheObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ExcludeRulesetCacheDataSource(private val realm: Realm) {

    suspend fun saveExcludeRuleset(ruleset: ExcludeRulesetCacheObject) {
        realm.write {
            val currentRuleset = query<ExcludeRulesetCacheObject>("id = $0", ruleset.id)
                .first()
                .find()

            currentRuleset?.let {
                // Update existing ruleset
                it.ips = ruleset.ips
                it.domains = ruleset.domains
            } ?: run {
                // Save new ruleset
                copyToRealm(ruleset)
            }
        }
    }

    suspend fun getExcludeRuleset(id: String): ExcludeRulesetCacheObject? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val ruleset = realm.query<ExcludeRulesetCacheObject>("id = $0", id)
                    .first()
                    .find()
                    ?.copyFromRealm()

                continuation.resume(ruleset)
            }
        }
    }
}
