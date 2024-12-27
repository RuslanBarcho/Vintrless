package pw.vintr.vintrless.presentation.screen.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.presentation.theme.*
import pw.vintr.vintrless.presentation.uikit.menu.CardMenu
import pw.vintr.vintrless.presentation.uikit.menu.CardMenuItemData
import pw.vintr.vintrless.presentation.uikit.navbar.NAV_BAR_HEIGHT_DP
import pw.vintr.vintrless.presentation.uikit.navbar.NAV_BAR_PADDING_DP
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.settings

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val screenState = viewModel.screenState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VintrlessExtendedTheme.colors.screenBackgroundColor)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                top = 32.dp,
                start = 28.dp,
                end = 28.dp,
                bottom = NAV_BAR_HEIGHT_DP.dp + NAV_BAR_PADDING_DP.dp + 32.dp
            ),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(Res.string.settings),
            style = Gilroy32(),
            color = VintrlessExtendedTheme.colors.textRegular,
        )
        Spacer(modifier = Modifier.height(32.dp))
        CardMenu(
            itemsData = screenState.value.items.map { settingsItem ->
                CardMenuItemData(
                    title = stringResource(settingsItem.titleRes),
                    description = settingsItem.descriptionRes?.let { resource ->
                        stringResource(
                            resource,
                            *settingsItem.descriptionArgs.toTypedArray()
                        )
                    } ?: settingsItem.descriptionText.orEmpty(),
                ) {
                    viewModel.onSettingItemClick(settingsItem)
                }
            }
        )
    }
}
