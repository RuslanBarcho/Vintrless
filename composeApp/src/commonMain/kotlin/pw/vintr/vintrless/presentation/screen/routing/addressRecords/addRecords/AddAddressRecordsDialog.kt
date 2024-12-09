package pw.vintr.vintrless.presentation.screen.routing.addressRecords.addRecords

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.theme.RubikMedium16
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.switchColors
import pw.vintr.vintrless.presentation.uikit.button.ButtonCloseDialog
import pw.vintr.vintrless.presentation.uikit.menu.MenuActionWithIcon
import pw.vintr.vintrless.presentation.uikit.separator.LineSeparator
import pw.vintr.vintrless.tools.extensions.cardBackground
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.enter_manually
import vintrless.composeapp.generated.resources.ic_clipboard
import vintrless.composeapp.generated.resources.ic_edit
import vintrless.composeapp.generated.resources.ic_file
import vintrless.composeapp.generated.resources.load_from_file
import vintrless.composeapp.generated.resources.replace_current

@Composable
fun AddAddressRecordsDialog(
    viewModel: AddAddressRecordsViewModel = koinViewModel()
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            MenuActionWithIcon(
                iconRes = Res.drawable.ic_file,
                title = stringResource(Res.string.load_from_file)
            ) {
                viewModel.pickFile()
            }
            LineSeparator(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
            )
            MenuActionWithIcon(
                iconRes = Res.drawable.ic_clipboard,
                title = stringResource(Res.string.paste_from_clipboard)
            ) {

            }
            LineSeparator(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
            )
            MenuActionWithIcon(
                iconRes = Res.drawable.ic_edit,
                title = stringResource(Res.string.enter_manually)
            ) {

            }
            Spacer(modifier = Modifier.height(20.dp))
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
        }
    }
}
