package pw.vintr.vintrless.presentation.screen.routing.addressRecords.manualInputRecords

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.Comma
import pw.vintr.vintrless.tools.extensions.Empty

class ManualInputAddressRecordsViewModel(
    defaultReplaceCurrent: Boolean,
    navigator: AppNavigator,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(ManualInputRecordsState(
        replaceCurrent = defaultReplaceCurrent,
    ))
    val screenState = _screenState.asStateFlow()

    fun setInput(value: String) {
        _screenState.update { it.copy(input = value) }
    }

    fun setReplaceCurrent(value: Boolean) {
        _screenState.update { it.copy(replaceCurrent = value) }
    }

    fun confirm() {
        val records = _screenState.value.input
            .split(String.Comma)
            .map { it.trim() }
        val replaceCurrent = _screenState.value.replaceCurrent

        if (records.isNotEmpty()) {
            navigator.back(
                resultKey = ManualInputAddressRecordsResult.KEY,
                result = ManualInputAddressRecordsResult(
                    records = records,
                    replaceCurrent = replaceCurrent
                )
            )
        }
    }
}

data class ManualInputRecordsState(
    val input: String = String.Empty,
    val replaceCurrent: Boolean = false,
) {
    val formIsValid: Boolean = input.isNotEmpty()
}
