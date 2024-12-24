package pw.vintr.vintrless.presentation.screen.routing.addressRecords.manualInputRecords

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.uikit.button.ButtonCloseDialog
import pw.vintr.vintrless.presentation.uikit.input.AppTextField
import pw.vintr.vintrless.tools.extensions.cardBackground
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.field_not_specified
import vintrless.composeapp.generated.resources.manual_input_label

@Composable
fun ManualInputRecordsDialog(
    viewModel: ManualInputRecordsViewModel = koinViewModel()
) {
    val screenState = viewModel.screenState.collectAsState()

    Column(
        modifier = Modifier
            .cardBackground(cornerRadius = 20.dp)
            .padding(vertical = 24.dp)
    ) {
        ButtonCloseDialog {
            viewModel.navigateBack(NavigatorType.Root)
        }
        AppTextField(
            modifier = Modifier
                .fillMaxWidth(),
            label = stringResource(Res.string.manual_input_label),
            value = screenState.value.input,
            hint = stringResource(Res.string.field_not_specified),
            singleLine = false,
            onValueChange = { viewModel.setInput(it) }
        )
    }
}
