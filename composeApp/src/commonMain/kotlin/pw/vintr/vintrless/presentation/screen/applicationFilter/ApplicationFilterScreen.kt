package pw.vintr.vintrless.presentation.screen.applicationFilter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
import pw.vintr.vintrless.platform.manager.UserApplicationsManager
import pw.vintr.vintrless.presentation.theme.*
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondary
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondarySize
import pw.vintr.vintrless.presentation.uikit.container.RestrictedWidthLayout
import pw.vintr.vintrless.presentation.uikit.layout.ScreenStateLayout
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import pw.vintr.vintrless.tools.extensions.selectableCardBackground
import pw.vintr.vintrless.tools.painter.suspendBitmapPainter
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_delete
import vintrless.composeapp.generated.resources.apps_filter_title

@Composable
fun ApplicationFilterScreen(
    viewModel: ApplicationFilterViewModel = koinViewModel()
) {
    val screenState = viewModel.screenState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            ToolbarRegular(
                title = stringResource(Res.string.apps_filter_title),
                onBackPressed = { viewModel.navigateBack() },
            )
        },
    ) { scaffoldPadding ->
        RestrictedWidthLayout(
            restrictionWidth = 800.dp
        ) { constraints ->
            val density = LocalDensity.current
            val wideScreen = remember(constraints) {
                with(density) { constraints.maxWidth.toDp() } > 650.dp
            }

            ScreenStateLayout(
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .fillMaxSize(),
                state = screenState.value
            ) { state ->
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(
                        vertical = 20.dp,
                        horizontal = 28.dp,
                    ),
                    columns = GridCells.Fixed(count = 2),
                ) {
                    items(
                        items = state.payload.userInstalledApplications,
                        span = { GridItemSpan(if (wideScreen) 1 else 2) },
                    ) { application ->
                        ApplicationCard(
                            application = application,
                            manuallyAdded = false,
                            selected = false,
                            onSelectClick = {
                                clipboardManager.setText(AnnotatedString(application.name))
                            },
                            onDeleteClick = {  },
                        )
                    }
                }
            }
        }
    }
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
            .height(IntrinsicSize.Max)
            .selectableCardBackground(selected = selected)
            .clickable { onSelectClick() }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Application icon (optional)
        Image(
            modifier = Modifier
                .size(48.dp),
            painter = suspendBitmapPainter {
                UserApplicationsManager.getApplicationIcon(application)
            },
            contentDescription = null,
        )
        Spacer(Modifier.width(20.dp))

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
                style = RubikMedium12(),
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
