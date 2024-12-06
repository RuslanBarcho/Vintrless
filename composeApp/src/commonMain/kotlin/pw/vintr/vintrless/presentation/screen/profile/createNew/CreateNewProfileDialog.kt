package pw.vintr.vintrless.presentation.screen.profile.createNew

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.theme.Gilroy12
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.menu.MenuAction
import pw.vintr.vintrless.presentation.uikit.menu.MenuActionWithIcon
import pw.vintr.vintrless.presentation.uikit.separator.LineSeparator
import pw.vintr.vintrless.tools.extensions.cardBackground
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_clipboard
import vintrless.composeapp.generated.resources.ic_close
import vintrless.composeapp.generated.resources.ic_qr_scan

@Composable
fun CreateNewProfileDialog(
    viewModel: CreateNewProfileViewModel = koinViewModel()
) {
    val screenState = viewModel.screenState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .cardBackground(cornerRadius = 20.dp)
            .padding(vertical = 24.dp)
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .align(Alignment.End)
                .size(24.dp)
                .clip(CircleShape)
                .clickable { viewModel.navigateBack(NavigatorType.Root) },
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = null,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            // Auto detect section
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = stringResource(Res.string.auto_detect),
                style = Gilroy12(),
                color = VintrlessExtendedTheme.colors.textLabel,
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (screenState.value.qrScanAvailable) {
                MenuActionWithIcon(
                    iconRes = Res.drawable.ic_qr_scan,
                    title = stringResource(Res.string.scan_qr)
                ) {
                    viewModel.openQRScan()
                }
                LineSeparator(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                )
            }
            MenuActionWithIcon(
                iconRes = Res.drawable.ic_clipboard,
                title = stringResource(Res.string.paste_from_clipboard)
            ) {
                viewModel.pasteFromClipboard(
                    pasteText = clipboardManager.getText()?.text.orEmpty()
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Manual input section
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = stringResource(Res.string.manual_input),
                style = Gilroy12(),
                color = VintrlessExtendedTheme.colors.textLabel,
            )
            Spacer(modifier = Modifier.height(10.dp))
            screenState.value.availableForms.mapIndexed { index, form ->
                MenuAction(
                    title = form.type.protocolName,
                ) {
                    viewModel.openFillProfileForm(form)
                }
                if (index != screenState.value.availableForms.lastIndex) {
                    LineSeparator(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                    )
                }
            }
        }
    }
}
