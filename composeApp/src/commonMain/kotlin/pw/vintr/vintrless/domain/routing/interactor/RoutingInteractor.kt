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
        val savedBlacklistRuleset = routingRepository
            .getExcludeRuleset(Ruleset.Exclude.Type.BLACKLIST.id)
        val blacklistRuleset = Ruleset.Exclude(
            type = Ruleset.Exclude.Type.BLACKLIST,
            ips = savedBlacklistRuleset?.ips?.toList().orEmpty(),
            domains = savedBlacklistRuleset?.domains?.toList().orEmpty()
        )

        // Default whitelist
        val savedWhitelistRuleset = routingRepository
            .getExcludeRuleset(Ruleset.Exclude.Type.WHITELIST.id)
        val whitelistRuleset = Ruleset.Exclude(
            type = Ruleset.Exclude.Type.WHITELIST,
            ips = savedWhitelistRuleset?.ips?.toList().orEmpty(),
            domains = savedWhitelistRuleset?.domains?.toList().orEmpty()
        )

        return listOf(
            global,
            blacklistRuleset,
            whitelistRuleset
        )
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
