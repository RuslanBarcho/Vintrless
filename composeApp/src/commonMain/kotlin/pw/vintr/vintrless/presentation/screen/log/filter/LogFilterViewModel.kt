package pw.vintr.vintrless.presentation.screen.log.filter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pw.vintr.vintrless.domain.log.model.LogFilter
import pw.vintr.vintrless.domain.log.model.LogType
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.Empty

class LogFilterViewModel(
    navigator: AppNavigator,
    logFilter: LogFilter,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(LogFilterState(
        query = logFilter.query,
        selection = logFilter.selection,
    ))
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
        navigator.back(
            resultKey = LogFilterResult.KEY,
            result = LogFilterResult(
                query = _screenState.value.query,
                selection = _screenState.value.selection,
            ),
        )
    }
}

data class LogFilterState(
    val query: String = String.Empty,
    val selection: Map<LogType, Boolean> = LogFilter.defaultTypeSelection
) {
    val formIsValid: Boolean = selection.any { it.value }
}
