package pw.vintr.vintrless.presentation.screen.routing.addressRecords.addRecords

import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.Comma

class AddAddressRecordsViewModel(
    navigator: AppNavigator,
) : BaseViewModel(navigator) {

    companion object {
        private const val TXT_EXTENSION = "txt"
    }

    private val _screenState = MutableStateFlow(AddAddressRecordsState())
    val screenState = _screenState.asStateFlow()

    fun pickFile() {
        launch(createExceptionHandler()) {
            val file = FileKit.pickFile(
                type = PickerType.File(extensions = listOf(TXT_EXTENSION)),
                mode = PickerMode.Single,
            )
            val bytes = file?.readBytes() ?: return@launch
            val items = bytes.decodeToString()
                .split(String.Comma)

            closeAndSendResult(items)
        }
    }

    fun parseFromClipboard(clipboardContent: String) {
        if (clipboardContent.isEmpty()) { return }
        val items = clipboardContent
            .split(String.Comma)

        closeAndSendResult(items)
    }

    fun setReplaceCurrent(value: Boolean) {
        _screenState.update { it.copy(replaceCurrent = value) }
    }

    private fun closeAndSendResult(items: List<String>) {
        if (items.isEmpty()) { return }

        navigator.back(
            resultKey = AddAddressRecordsResult.KEY,
            result = AddAddressRecordsResult.RecordsSelected(
                records = items,
                replaceCurrent = _screenState.value.replaceCurrent
            )
        )
    }
}

data class AddAddressRecordsState(
    val replaceCurrent: Boolean = false,
)
