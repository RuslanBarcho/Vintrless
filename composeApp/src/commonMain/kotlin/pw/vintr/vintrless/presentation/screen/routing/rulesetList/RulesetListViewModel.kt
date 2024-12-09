package pw.vintr.vintrless.presentation.screen.routing.rulesetList

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.routing.interactor.RoutingInteractor
import pw.vintr.vintrless.domain.routing.model.Ruleset
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.tools.extensions.updateLoaded

class RulesetListViewModel(
    navigator: AppNavigator,
    private val routingInteractor: RoutingInteractor,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow<BaseScreenState<ProfileListState>>(BaseScreenState.Loading())
    val screenState = _screenState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _screenState.loadWithStateHandling {
            val rulesets = routingInteractor.getRulesets()
            val selectedRulesetId = routingInteractor.getSelectedRulesetId()

            ProfileListState(
                rulesets = rulesets,
                selectedRulesetId = selectedRulesetId,
            )
        }
    }

    fun selectRuleset(ruleset: Ruleset) {
        launch {
            routingInteractor.setSelectedRulesetId(ruleset.id)
            _screenState.updateLoaded { it.copy(selectedRulesetId = ruleset.id) }
        }
    }

    fun openEditRuleset(ruleset: Ruleset) {
        when (ruleset) {
            is Ruleset.Exclude -> {
                navigator.forward(AppScreen.EditAddressRecords(ruleset.id))
            }
            else -> Unit
        }
    }

    fun onDeleteClick(ruleset: Ruleset) {
        // TODO: delete
    }
}

data class ProfileListState(
    val rulesets: List<Ruleset>,
    val selectedRulesetId: String,
)
