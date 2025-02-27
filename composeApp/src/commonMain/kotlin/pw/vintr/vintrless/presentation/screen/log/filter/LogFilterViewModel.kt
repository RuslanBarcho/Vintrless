package pw.vintr.vintrless.presentation.screen.log.filter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pw.vintr.vintrless.domain.log.model.LogType
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.Empty

class LogFilterViewModel(
    navigator: AppNavigator,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(LogFilterState())
    val screenState = _screenState.asStateFlow()

    fun setQuery(value: String) {
        _screenState.update { it.copy(query = value) }
    }

    fun setSelection(type: LogType, value: Boolean) {
        _screenState.update { state ->
            state.copy(
                selection = state.selection
                    .toMutableMap()
                    .apply { this[type] = value }
            )
        }
    }

    fun apply() {
        // TODO: apply filter
    }
}

data class LogFilterState(
    val query: String = String.Empty,
    val selection: Map<LogType, Boolean> = mapOf(
        LogType.INFORMATION to true,
        LogType.ERROR to true,
        LogType.WARNING to true,
    )
) {
    val formIsValid: Boolean = selection.any { it.value }
}
