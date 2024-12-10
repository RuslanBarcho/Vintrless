package pw.vintr.vintrless.domain.routing.interactor

import pw.vintr.vintrless.data.routing.repository.RoutingRepository
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.routing.model.Ruleset

class RoutingInteractor(
    private val routingRepository: RoutingRepository
) : BaseInteractor() {

    suspend fun getRulesets(): List<Ruleset> {
        // Default global
        val global = Ruleset.Global

        // Default blacklist
        val blacklistRuleset = getExcludeRuleset(Ruleset.Exclude.Type.BLACKLIST)

        // Default whitelist
        val whitelistRuleset = getExcludeRuleset(Ruleset.Exclude.Type.WHITELIST)

        return listOf(
            global,
            blacklistRuleset,
            whitelistRuleset
        )
    }

    suspend fun getExcludeRuleset(type: Ruleset.Exclude.Type): Ruleset.Exclude {
        val savedWhitelistRuleset = routingRepository
            .getExcludeRuleset(type.id)
        return Ruleset.Exclude(
            type = type,
            ips = savedWhitelistRuleset?.ips?.toList().orEmpty(),
            domains = savedWhitelistRuleset?.domains?.toList().orEmpty()
        )
    }

    suspend fun getSelectedRuleset(): Ruleset {
        val selectedId = getSelectedRulesetId()

        return when (selectedId) {
            Ruleset.Global.id -> {
                Ruleset.Global
            }
            Ruleset.Exclude.Type.BLACKLIST.id -> {
                getExcludeRuleset(Ruleset.Exclude.Type.BLACKLIST)
            }
            Ruleset.Exclude.Type.WHITELIST.id -> {
                getExcludeRuleset(Ruleset.Exclude.Type.WHITELIST)
            }
            else -> {
                Ruleset.Global
            }
        }
    }

    suspend fun setSelectedRulesetId(id: String) {
        routingRepository.setSelectedRulesetId(id)
    }

    suspend fun getSelectedRulesetId(): String {
        return routingRepository.getSelectedRulesetId() ?: Ruleset.Global.id
    }

    suspend fun saveExcludeRuleset(ruleset: Ruleset.Exclude) {
        routingRepository.saveExcludeRuleset(ruleset.toCacheObject())
    }
}
