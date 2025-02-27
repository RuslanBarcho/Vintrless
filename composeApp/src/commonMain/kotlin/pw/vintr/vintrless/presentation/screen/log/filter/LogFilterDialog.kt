package pw.vintr.vintrless.presentation.screen.log.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.domain.log.model.LogType
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.theme.AppColor.RadicalRed
import pw.vintr.vintrless.presentation.theme.AppColor.Zest
import pw.vintr.vintrless.presentation.theme.Gilroy16
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.checkboxColors
import pw.vintr.vintrless.presentation.uikit.button.ButtonRegular
import pw.vintr.vintrless.presentation.uikit.button.ButtonRegularSize
import pw.vintr.vintrless.presentation.uikit.input.AppTextField
import pw.vintr.vintrless.presentation.uikit.separator.LineSeparator
import pw.vintr.vintrless.tools.extensions.cardBackground
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_close

@Composable
fun LogFilterDialog(
    viewModel: LogFilterViewModel = koinViewModel(),
    availableLogTypes: List<LogType> = listOf(LogType.INFORMATION, LogType.ERROR, LogType.WARNING)
) {
    val screenState = viewModel.screenState.collectAsState()

    Column(
        modifier = Modifier
            .cardBackground(cornerRadius = 20.dp)
            .padding(vertical = 24.dp)
    ) {
        // Close icon
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
        Spacer(modifier = Modifier.height(16.dp))

        // Search query field
        AppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            label = stringResource(Res.string.logs_filter_query_label),
            value = screenState.value.query,
            hint = stringResource(Res.string.logs_filter_query_hint),
            onValueChange = { viewModel.setQuery(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LineSeparator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Filter items
        availableLogTypes.forEach { type ->
            LogFilterItem(
                type = type,
                selected = screenState.value.selection[type] ?: true,
            ) { value ->
                viewModel.setSelection(type, value)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Apply button
        ButtonRegular(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            text = stringResource(Res.string.common_apply),
            enabled = screenState.value.formIsValid,
            size = ButtonRegularSize.MEDIUM,
        ) {
            viewModel.apply()
        }
    }
}

@Composable
private fun LogFilterItem(
    modifier: Modifier = Modifier,
    type: LogType,
    selected: Boolean,
    onSelectClick: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelectClick(!selected) }
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Type title
        Text(
            modifier = Modifier
                .weight(1f),
            text = stringResource(
                when (type) {
                    LogType.INFORMATION -> Res.string.logs_type_info
                    LogType.WARNING -> Res.string.logs_type_warnings
                    LogType.ERROR -> Res.string.logs_type_errors
                }
            ),
            color = when (type) {
                LogType.INFORMATION -> VintrlessExtendedTheme.colors.textRegular
                LogType.WARNING -> Zest
                LogType.ERROR -> RadicalRed
            },
            style = Gilroy16()
        )
        Spacer(modifier = Modifier.width(10.dp))

        // Select type checkbox
        Checkbox(
            checked = selected,
            colors = checkboxColors(),
            onCheckedChange = { onSelectClick(it) },
        )
    }
}
