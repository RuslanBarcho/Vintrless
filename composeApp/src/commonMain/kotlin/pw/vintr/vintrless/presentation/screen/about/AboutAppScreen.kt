package pw.vintr.vintrless.presentation.screen.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.presentation.theme.Gilroy12
import pw.vintr.vintrless.presentation.theme.Gilroy16
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.menu.CardMenu
import pw.vintr.vintrless.presentation.uikit.menu.CardMenuItemData
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.settings_about_title
import vintrless.composeapp.generated.resources.ic_app_icon
import vintrless.composeapp.generated.resources.about_app_snippet
import vintrless.composeapp.generated.resources.ic_telegram
import vintrless.composeapp.generated.resources.about_app_telegram_title
import vintrless.composeapp.generated.resources.about_app_telegram_description
import vintrless.composeapp.generated.resources.ic_github
import vintrless.composeapp.generated.resources.about_app_github_title
import vintrless.composeapp.generated.resources.about_app_github_description

private const val TELEGRAM_URL = "https://t.me/vintrless"
private const val GITHUB_URL = "https://github.com/RuslanBarcho/Vintrless"

@Composable
fun AboutAppScreen(
    viewModel: AboutAppViewModel = koinViewModel()
) {
    val screenState = viewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            ToolbarRegular(
                title = stringResource(Res.string.settings_about_title),
                onBackPressed = { viewModel.navigateBack() },
            )
        },
    ) { scaffoldPadding ->
        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    vertical = 20.dp,
                    horizontal = 28.dp,
                )
        ) {
            AboutHeader(
                appVersion = screenState.value.appVersion
            )
            Spacer(modifier = Modifier.height(28.dp))
            CardMenu(
                itemsData = listOf(
                    CardMenuItemData(
                        iconRes = Res.drawable.ic_telegram,
                        title = stringResource(Res.string.about_app_telegram_title),
                        description = stringResource(Res.string.about_app_telegram_description),
                    ) {
                        uriHandler.openUri(TELEGRAM_URL)
                    },
                    CardMenuItemData(
                        iconRes = Res.drawable.ic_github,
                        title = stringResource(Res.string.about_app_github_title),
                        description = stringResource(Res.string.about_app_github_description),
                    ) {
                        uriHandler.openUri(GITHUB_URL)
                    },
                )
            )
        }
    }
}

@Composable
private fun AboutHeader(
    modifier: Modifier = Modifier,
    appVersion: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(48.dp),
            painter = painterResource(Res.drawable.ic_app_icon),
            contentDescription = null,
        )
        Spacer(Modifier.width(28.dp))
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(Res.string.about_app_snippet),
                style = Gilroy16(),
                color = VintrlessExtendedTheme.colors.textRegular,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = appVersion,
                style = Gilroy12(),
                color = VintrlessExtendedTheme.colors.textSecondary,
            )
        }
    }
}
