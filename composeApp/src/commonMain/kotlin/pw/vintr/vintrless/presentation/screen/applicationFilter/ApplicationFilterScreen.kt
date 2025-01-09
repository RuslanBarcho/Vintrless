package pw.vintr.vintrless.presentation.screen.applicationFilter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
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
            val columnsCount = if (wideScreen) 2 else 1

            ScreenStateLayout(
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .fillMaxSize(),
                state = screenState.value
            ) { state ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(
                        vertical = 20.dp,
                        horizontal = 28.dp,
                    ),
                ) {
                    val installedApplicationsCount = state.payload.userInstalledApplications.size
                    val rowsCount = (installedApplicationsCount + 1) / columnsCount

                    items(count = rowsCount) { i ->
                        Row(Modifier.height(IntrinsicSize.Max)) {
                            for (j in 0 until columnsCount) {
                                val index = i * columnsCount + j

                                if (index < installedApplicationsCount) {
                                    val application = state.payload.userInstalledApplications[index]

                                    ApplicationCard(
                                        modifier = Modifier.weight(1f),
                                        application = application,
                                        manuallyAdded = false,
                                        selected = false,
                                        onSelectClick = {  },
                                        onDeleteClick = {  },
                                    )

                                    if (j < (columnsCount - 1)) {
                                        Spacer(Modifier.width(20.dp))
                                    }
                                } else {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
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
            .fillMaxHeight()
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
                text = application.payload.payloadReadableTitle,
                style = RubikMedium12(),
                color = VintrlessExtendedTheme.colors.textRegular,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
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
