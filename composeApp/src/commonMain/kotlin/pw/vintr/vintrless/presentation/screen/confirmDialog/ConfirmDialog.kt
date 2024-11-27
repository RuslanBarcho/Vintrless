package pw.vintr.vintrless.presentation.screen.confirmDialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.presentation.theme.Gilroy20
import pw.vintr.vintrless.presentation.theme.RubikRegular14
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.button.ButtonText
import pw.vintr.vintrless.tools.extensions.cardBackground
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.common_cancel
import vintrless.composeapp.generated.resources.common_ok

@Composable
fun ConfirmDialog(
    data: ConfirmDialogData,
    viewModel: ConfirmViewModel = koinViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .cardBackground(cornerRadius = 20.dp)
            .padding(24.dp)
    ) {
        Text(
            text = when (data) {
                is ConfirmDialogData.Resource -> {
                    stringResource(data.titleRes)
                }
                is ConfirmDialogData.Text -> {
                    data.title
                }
            },
            color = VintrlessExtendedTheme.colors.textRegular,
            style = Gilroy20()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = when (data) {
                is ConfirmDialogData.Resource -> {
                    stringResource(data.messageRes)
                }
                is ConfirmDialogData.Text -> {
                    data.message
                }
            },
            color = VintrlessExtendedTheme.colors.textSecondary,
            style = RubikRegular14()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ButtonText(
                modifier = Modifier.weight(1f),
                text = when (data) {
                    is ConfirmDialogData.Resource -> {
                        data.declineTextRes?.let { stringResource(it) }

                    }
                    is ConfirmDialogData.Text -> {
                        data.declineText
                    }
                } ?: stringResource(Res.string.common_cancel),
                onClick = { viewModel.decline() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            ButtonText(
                modifier = Modifier.weight(1f),
                text = when (data) {
                    is ConfirmDialogData.Resource -> {
                        data.acceptTextRes?.let { stringResource(it) }

                    }
                    is ConfirmDialogData.Text -> {
                        data.acceptText
                    }
                } ?: stringResource(Res.string.common_ok),
                onClick = { viewModel.accept() }
            )
        }
    }
}
