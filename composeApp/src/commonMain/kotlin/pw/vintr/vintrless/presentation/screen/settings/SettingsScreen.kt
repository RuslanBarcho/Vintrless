package pw.vintr.vintrless.presentation.screen.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.presentation.theme.*
import pw.vintr.vintrless.presentation.uikit.navbar.NAV_BAR_HEIGHT_DP
import pw.vintr.vintrless.presentation.uikit.navbar.NAV_BAR_PADDING_DP
import pw.vintr.vintrless.presentation.uikit.separator.LineSeparator
import pw.vintr.vintrless.tools.extensions.cardBackground
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_arrow_right
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .cardShadow(cornersRadius = 20.dp)
                .cardBackground(cornerRadius = 20.dp)
                .padding(vertical = 12.dp)
        ) {
            screenState.value.items.forEachIndexed { index, settingsItem ->
                SettingsItemView(
                    item = settingsItem,
                ) {
                    viewModel.onSettingItemClick(settingsItem)
                }
                if (index != screenState.value.items.lastIndex) {
                    LineSeparator(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsItemView(
    item: SettingsItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 32.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(item.titleRes),
                style = Gilroy16(),
                color = VintrlessExtendedTheme.colors.textRegular,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = item.descriptionRes?.let { resource ->
                    stringResource(
                        resource,
                        *item.descriptionArgs.toTypedArray()
                    )
                } ?: item.descriptionText.orEmpty(),
                style = Gilroy12(),
                color = VintrlessExtendedTheme.colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
        )
    }
}
