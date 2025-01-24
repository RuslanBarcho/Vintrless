package pw.vintr.vintrless.presentation.screen.applicationFilter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
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
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterMode
import pw.vintr.vintrless.domain.userApplications.model.common.IDeviceApplication
import pw.vintr.vintrless.domain.userApplications.model.common.process.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.common.application.UserApplication
import pw.vintr.vintrless.platform.manager.UserApplicationsManager
import pw.vintr.vintrless.presentation.theme.*
import pw.vintr.vintrless.presentation.uikit.button.ButtonRegular
import pw.vintr.vintrless.presentation.uikit.button.ButtonRegularSize
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondary
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondarySize
import pw.vintr.vintrless.presentation.uikit.container.RestrictedWidthLayout
import pw.vintr.vintrless.presentation.uikit.input.AppDropdownEditableField
import pw.vintr.vintrless.presentation.uikit.input.AppDropdownField
import pw.vintr.vintrless.presentation.uikit.input.AppTextField
import pw.vintr.vintrless.presentation.uikit.input.DropdownPayload
import pw.vintr.vintrless.presentation.uikit.layout.ScreenStateLayout
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import pw.vintr.vintrless.tools.extensions.Empty
import pw.vintr.vintrless.tools.extensions.cardBackground
import pw.vintr.vintrless.tools.extensions.selectableCardBackground
import pw.vintr.vintrless.tools.painter.suspendBitmapPainter
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.apps_filter_blacklist
import vintrless.composeapp.generated.resources.apps_filter_enable_setting
import vintrless.composeapp.generated.resources.apps_filter_title
import vintrless.composeapp.generated.resources.apps_filter_whitelist
import vintrless.composeapp.generated.resources.ic_delete

private const val KEY_FILTER_STATUS = "key-filter-status"

private const val KEY_PROCESS_ADD_FORM = "key-process-add-form"

private const val KEY_SEARCH = "key-search"

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
                    .fillMaxSize()
                    .imePadding(),
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
                    // Filter status and mode
                    item(KEY_FILTER_STATUS) {
                        ApplicationFilterSettings(
                            enabled = state.payload.enabled,
                            availableModes = state.payload.availableFilterModes,
                            selectedMode = state.payload.selectedFilterMode,
                            onEnableStateChange = {
                                viewModel.setEnabled(it)
                            },
                            onFilterModeSelected = {
                                viewModel.setFilterMode(it)
                            },
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    // Process add form (desktop only)
                    if (state.payload.processAddFormState.enabled) {
                        item(KEY_PROCESS_ADD_FORM) {
                            ProcessAddForm(
                                state = state.payload.processAddFormState,
                                onAppNameChange = {
                                    viewModel.setAddFormAppName(it)
                                },
                                onProcessNameChange = {
                                    viewModel.setAddFormProcessName(it)
                                },
                                onProcessSelect = {
                                    viewModel.setAddFormValue(it)
                                },
                                onSaveClick = {
                                    viewModel.saveProcess()
                                },
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    // Search apps field
                    item(KEY_SEARCH) {
                        SearchField(
                            value = state.payload.searchValue,
                            onValueChange = { viewModel.setSearchValue(it) }
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    // Installed apps on device
                    val installedUserApplicationsCount = state.payload.filteredUserInstalledApplications.size
                    val rowsCount = (installedUserApplicationsCount + 1) / columnsCount

                    items(
                        count = rowsCount,
                        key = { i ->
                            // Row key is a composition of row's items uuids
                            var key = String.Empty

                            for (j in 0 until columnsCount) {
                                val itemIndex = i * columnsCount + j
                                if (itemIndex < installedUserApplicationsCount) {
                                    key += state.payload.filteredUserInstalledApplications[itemIndex]
                                        .lazyListUUID
                                }
                            }

                            key
                        }
                    ) { i ->
                        Row(Modifier.height(IntrinsicSize.Max)) {
                            for (j in 0 until columnsCount) {
                                val itemIndex = i * columnsCount + j

                                if (itemIndex < installedUserApplicationsCount) {
                                    val application = state.payload.filteredUserInstalledApplications[itemIndex]

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
private fun ApplicationFilterSettings(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    availableModes: List<ApplicationFilterMode>,
    selectedMode: ApplicationFilterMode,
    onEnableStateChange: (Boolean) -> Unit,
    onFilterModeSelected: (ApplicationFilterMode) -> Unit,
) {
    val modePayloads = availableModes.map { mode ->
        DropdownPayload<ApplicationFilterMode?>(
            payload = mode,
            title = when (mode) {
                ApplicationFilterMode.BLACKLIST -> {
                    stringResource(Res.string.apps_filter_blacklist)
                }
                ApplicationFilterMode.WHITELIST -> {
                    stringResource(Res.string.apps_filter_whitelist)
                }
            }
        )
    }
    val selectedPayload = modePayloads
        .find { it.payload == selectedMode } ?: modePayloads.first()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardBackground()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = stringResource(Res.string.apps_filter_enable_setting),
                style = RubikMedium16(),
                color = VintrlessExtendedTheme.colors.textRegular,
            )
            Spacer(Modifier.width(10.dp))
            Switch(
                checked = enabled,
                onCheckedChange = onEnableStateChange,
                colors = switchColors()
            )
        }
        Spacer(Modifier.height(20.dp))
        AppDropdownField(
            modifier = Modifier
                .fillMaxWidth(),
            label = stringResource(Res.string.apps_filter_mode_title),
            items = modePayloads,
            selectedItem = selectedPayload,
            onItemSelected = { mode ->
                mode?.let { onFilterModeSelected(it) }
            }
        )
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            text = stringResource(
                when (selectedMode) {
                    ApplicationFilterMode.BLACKLIST -> {
                        Res.string.apps_filter_blacklist_description
                    }
                    ApplicationFilterMode.WHITELIST -> {
                        Res.string.apps_filter_whitelist_description
                    }
                }
            ),
            style = RubikMedium12(),
            color = VintrlessExtendedTheme.colors.textSecondary,
        )
    }
}

@Composable
private fun ProcessAddForm(
    modifier: Modifier = Modifier,
    state: ProcessAddFormState,
    onAppNameChange: (String) -> Unit,
    onProcessNameChange: (String) -> Unit,
    onProcessSelect: (SystemProcess) -> Unit,
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardBackground()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Title
        Text(
            text = stringResource(Res.string.apps_filter_add_process_manual),
            style = Gilroy18(),
            color = VintrlessExtendedTheme.colors.textRegular,
        )
        Spacer(Modifier.height(24.dp))

        // Application name field with process selection dropdown
        AppDropdownEditableField(
            modifier = Modifier
                .fillMaxWidth(),
            value = state.appNameValue,
            label = stringResource(Res.string.apps_filter_app_name),
            items = state.processesByAppName.map { process ->
                DropdownPayload(
                    title = process.appName,
                    payload = process,
                )
            },
            hint = stringResource(Res.string.field_not_specified),
            onValueChange = onAppNameChange,
            onItemSelected = { mode ->
                mode?.let { onProcessSelect(it) }
            }
        )
        Spacer(Modifier.height(24.dp))

        // Process name field with process selection dropdown
        AppDropdownEditableField(
            modifier = Modifier
                .fillMaxWidth(),
            value = state.processNameValue,
            label = stringResource(Res.string.apps_filter_process_name),
            items = state.processesByProcessName.map { process ->
                DropdownPayload(
                    title = process.processName,
                    payload = process,
                )
            },
            hint = stringResource(Res.string.field_not_specified),
            onValueChange = onProcessNameChange,
            onItemSelected = { mode ->
                mode?.let { onProcessSelect(it) }
            }
        )
        Spacer(Modifier.height(16.dp))

        // Save button
        ButtonRegular(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(Res.string.common_add),
            enabled = state.formIsValid,
            size = ButtonRegularSize.MEDIUM,
        ) {
            onSaveClick()
        }
    }
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
) {
    AppTextField(
        modifier = modifier
            .fillMaxWidth(),
        leadingIconRes = Res.drawable.ic_search,
        value = value,
        hint = stringResource(Res.string.common_search),
        showClearButton = value.isNotEmpty(),
        actionOnClear = { onValueChange(String.Empty) },
        onValueChange = onValueChange,
    )
}

@Composable
private fun ApplicationCard(
    modifier: Modifier = Modifier,
    application: IDeviceApplication,
    manuallyAdded: Boolean = false,
    selected: Boolean = false,
    onSelectClick: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .selectableCardBackground(selected = selected)
            .clickable { onSelectClick(!selected) }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Application icon (optional)
        if (application is UserApplication) {
            Image(
                modifier = Modifier
                    .size(48.dp),
                painter = suspendBitmapPainter(
                    placeholder = painterResource(Res.drawable.ic_application)
                ) {
                    UserApplicationsManager.getApplicationIcon(application)
                },
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
                text = application.appName,
                style = Gilroy16(),
                color = VintrlessExtendedTheme.colors.textRegular,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = application.processName,
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
            onCheckedChange = { onSelectClick(it) },
        )
    }
}
