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
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.screen.routing.addressRecords.editRecords.EditAddressRecordsResult
import pw.vintr.vintrless.tools.extensions.updateLoaded

class RulesetListViewModel(
    navigator: AppNavigator,
    private val routingInteractor: RoutingInteractor,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow<BaseScreenState<RulesetListState>>(BaseScreenState.Loading())
    val screenState = _screenState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _screenState.loadWithStateHandling { loadRulesetState() }
    }

    private suspend fun loadRulesetState(): RulesetListState {
        val rulesets = routingInteractor.getRulesets()
        val selectedRulesetId = routingInteractor.getSelectedRulesetId()

        return RulesetListState(
            rulesets = rulesets,
            selectedRulesetId = selectedRulesetId,
        )
    }

    fun selectRuleset(ruleset: Ruleset) {
        if (ruleset.isEmpty) {
            openEditRuleset(ruleset)
        } else {
            launch {
                routingInteractor.setSelectedRulesetId(ruleset.id)
                _screenState.updateLoaded { it.copy(selectedRulesetId = ruleset.id) }
            }
        }
    }

    fun openEditRuleset(ruleset: Ruleset) {
        when (ruleset) {
            is Ruleset.Exclude -> {
                openEditExcludeRuleset(ruleset)
            }
            else -> Unit
        }
    }

    private fun openEditExcludeRuleset(ruleset: Ruleset.Exclude) {
        handleResult(EditAddressRecordsResult.KEY) {
            navigator.forwardWithResult<EditAddressRecordsResult>(
                AppScreen.EditAddressRecords(ruleset.id),
                NavigatorType.Root,
                EditAddressRecordsResult.KEY
            ) { result ->
                handleEditExcludeRulesetResult(
                    ruleset = ruleset.copy(
                        ips = result.ips,
                        domains = result.domains,
                    )
                )
            }
        }
    }

    private fun handleEditExcludeRulesetResult(ruleset: Ruleset.Exclude) {
        // Save and refresh screen data
        launch {
            routingInteractor.saveExcludeRuleset(ruleset)
            _screenState.value = BaseScreenState.Loaded(loadRulesetState())
        }
    }

    fun onDeleteClick(ruleset: Ruleset) {
        // TODO: delete
    }
}

data class RulesetListState(
    val rulesets: List<Ruleset>,
    val selectedRulesetId: String,
)
