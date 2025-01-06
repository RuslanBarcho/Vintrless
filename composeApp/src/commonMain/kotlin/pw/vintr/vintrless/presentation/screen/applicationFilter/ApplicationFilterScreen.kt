package pw.vintr.vintrless.presentation.screen.applicationFilter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
import pw.vintr.vintrless.platform.manager.UserApplicationsManager
import pw.vintr.vintrless.presentation.theme.Gilroy16
import pw.vintr.vintrless.presentation.theme.RubikMedium14
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.checkboxColors
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondary
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondarySize
import pw.vintr.vintrless.tools.extensions.selectableCardBackground
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_delete

@Composable
fun ApplicationFilterScreen(
    viewModel: ApplicationFilterViewModel = koinViewModel()
) {

}

@Composable
private fun ApplicationCard(
    modifier: Modifier = Modifier,
    application: UserApplication,
    manuallyAdded: Boolean = false,
    selected: Boolean = false,
    onSelectClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableCardBackground(selected = selected)
            .clickable { onSelectClick() }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Application icon (optional)
        UserApplicationsManager.getApplicationIcon(application)?.let { iconBitmap ->
            Image(
                modifier = Modifier
                    .size(48.dp),
                bitmap = iconBitmap,
                contentDescription = null,
            )
            Spacer(Modifier.width(20.dp))
        }

        // App and process/package name
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = application.name,
                style = Gilroy16(),
                color = VintrlessExtendedTheme.colors.textRegular,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = application.processName,
                style = RubikMedium14(),
                color = VintrlessExtendedTheme.colors.textRegular,
            )
        }
        Spacer(Modifier.width(20.dp))

        // Delete button for manually added applications
        if (manuallyAdded) {
            ButtonSecondary(
                wrapContentWidth = true,
                size = ButtonSecondarySize.MEDIUM,
                content = {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(Res.drawable.ic_delete),
                        tint = VintrlessExtendedTheme.colors.secondaryButtonContent,
                        contentDescription = null
                    )
                }
            ) { onDeleteClick() }
            Spacer(Modifier.width(20.dp))
        }

        // Select checkbox
        Checkbox(
            checked = selected,
            colors = checkboxColors(),
            onCheckedChange = { onSelectClick() },
        )
    }
}
