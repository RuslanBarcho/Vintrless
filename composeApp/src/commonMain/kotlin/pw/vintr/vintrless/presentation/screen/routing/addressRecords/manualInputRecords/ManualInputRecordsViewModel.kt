package pw.vintr.vintrless.presentation.screen.routing.addressRecords.manualInputRecords

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.Empty

class ManualInputRecordsViewModel(
    navigator: AppNavigator,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(ManualInputRecordsState())
    val screenState = _screenState.asStateFlow()

    fun setInput(input: String) {
        _screenState.update { it.copy(input = input) }
    }
}

data class ManualInputRecordsState(
    val input: String = String.Empty,
    val replaceCurrent: Boolean = false,
)
