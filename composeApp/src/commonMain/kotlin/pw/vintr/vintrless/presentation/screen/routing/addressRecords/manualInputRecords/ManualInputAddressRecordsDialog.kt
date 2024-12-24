package pw.vintr.vintrless.presentation.screen.routing.addressRecords.manualInputRecords

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parameterSetOf
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.theme.RubikMedium16
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.switchColors
import pw.vintr.vintrless.presentation.uikit.button.ButtonCloseDialog
import pw.vintr.vintrless.presentation.uikit.button.ButtonRegular
import pw.vintr.vintrless.presentation.uikit.button.ButtonRegularSize
import pw.vintr.vintrless.presentation.uikit.input.AppTextField
import pw.vintr.vintrless.tools.extensions.cardBackground
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.field_not_specified
import vintrless.composeapp.generated.resources.manual_input_label
import vintrless.composeapp.generated.resources.replace_current

@Composable
fun ManualInputAddressRecordsDialog(
    defaultReplaceCurrent: Boolean,
    viewModel: ManualInputAddressRecordsViewModel = koinViewModel {
        parameterSetOf(defaultReplaceCurrent)
    }
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
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp)
                .padding(horizontal = 32.dp),
            label = stringResource(Res.string.manual_input_label),
            value = screenState.value.input,
            hint = stringResource(Res.string.field_not_specified),
            singleLine = false,
            onValueChange = { viewModel.setInput(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.replace_current),
                style = RubikMedium16(),
                color = VintrlessExtendedTheme.colors.textRegular,
            )
            Spacer(Modifier.width(10.dp))
            Switch(
                checked = screenState.value.replaceCurrent,
                onCheckedChange = { viewModel.setReplaceCurrent(it) },
                colors = switchColors()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ButtonRegular(
            modifier = Modifier
                .padding(horizontal = 32.dp),
            text = stringResource(Res.string.common_save),
            enabled = screenState.value.formIsValid,
            size = ButtonRegularSize.MEDIUM,
        ) {
            viewModel.confirm()
        }
    }
}
