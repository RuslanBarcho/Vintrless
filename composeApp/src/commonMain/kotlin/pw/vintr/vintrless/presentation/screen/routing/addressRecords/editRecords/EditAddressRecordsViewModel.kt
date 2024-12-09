package pw.vintr.vintrless.presentation.screen.routing.addressRecords.editRecords

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pw.vintr.vintrless.domain.routing.interactor.RoutingInteractor
import pw.vintr.vintrless.domain.routing.model.RuleAddressRecordType
import pw.vintr.vintrless.domain.routing.model.Ruleset
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.screen.routing.addressRecords.addRecords.AddAddressRecordsResult
import pw.vintr.vintrless.tools.extensions.updateLoaded
import pw.vintr.vintrless.tools.extensions.withLoaded

class EditAddressRecordsViewModel(
    navigator: AppNavigator,
    private val rulesetId: String,
    private val routingInteractor: RoutingInteractor,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow<BaseScreenState<EditAddressRecordsState>>(
        value = BaseScreenState.Loading()
    )
    val screenState = _screenState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _screenState.loadWithStateHandling {
            val ruleset = when (rulesetId) {
                Ruleset.Exclude.Type.BLACKLIST.id -> {
                    routingInteractor.getExcludeRuleset(Ruleset.Exclude.Type.BLACKLIST)
                }
                Ruleset.Exclude.Type.WHITELIST.id -> {
                    routingInteractor.getExcludeRuleset(Ruleset.Exclude.Type.WHITELIST)
                }
                else -> throw IllegalStateException("Illegal ruleset id for editing")
            }

            EditAddressRecordsState(
                ruleset = ruleset,
                ips = ruleset.ips,
                domains = ruleset.domains,
                selectedAddressRecordType = RuleAddressRecordType.IP
            )
        }
    }

    fun openAddAddressRecords() {
        _screenState.withLoaded { loadedState ->
            handleResult(AddAddressRecordsResult.KEY) {
                navigator.forwardWithResult<AddAddressRecordsResult>(
                    AppScreen.AddAddressRecords,
                    NavigatorType.Root,
                    AddAddressRecordsResult.KEY
                ) {
                    handleAddAddressRecordsResult(
                        records = it.records,
                        replaceCurrent = it.replaceCurrent,
                        type = loadedState.selectedAddressRecordType,
                    )
                }
            }
        }
    }

    private fun handleAddAddressRecordsResult(
        records: List<String>,
        replaceCurrent: Boolean,
        type: RuleAddressRecordType,
    ) {
        _screenState.updateLoaded { state ->
            when (type) {
                RuleAddressRecordType.IP -> {
                    state.copy(ips = if (replaceCurrent) records else state.ips + records)
                }
                RuleAddressRecordType.DOMAIN -> {
                    state.copy(domains = if (replaceCurrent) records else state.domains + records)
                }
            }
        }
    }

    fun selectAddressRecordType(type: RuleAddressRecordType) {
        _screenState.updateLoaded { it.copy(selectedAddressRecordType = type) }
    }

    fun removeAddressRecord(itemType: RuleAddressRecordType, itemIndex: Int) {
        _screenState.updateLoaded {
            when (itemType) {
                RuleAddressRecordType.IP -> {
                    it.copy(ips = it.ips.toMutableList().apply { removeAt(itemIndex) })
                }
                RuleAddressRecordType.DOMAIN -> {
                    it.copy(domains = it.domains.toMutableList().apply { removeAt(itemIndex) })
                }
            }
        }
    }
}

data class EditAddressRecordsState(
    val ruleset: Ruleset,
    val ips: List<String>,
    val domains: List<String>,
    val selectedAddressRecordType: RuleAddressRecordType,
)
