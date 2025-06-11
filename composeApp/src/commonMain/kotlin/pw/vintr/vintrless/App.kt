package pw.vintr.vintrless

import androidx.compose.animation.*
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import pw.vintr.vintrless.domain.alert.interactor.AlertInteractor
import pw.vintr.vintrless.domain.alert.model.AlertModel.Type.*
import pw.vintr.vintrless.domain.alert.model.AlertState
import pw.vintr.vintrless.domain.log.model.LogFilter
import pw.vintr.vintrless.presentation.navigation.*
import pw.vintr.vintrless.presentation.screen.about.AboutAppScreen
import pw.vintr.vintrless.presentation.screen.applicationFilter.ApplicationFilterScreen
import pw.vintr.vintrless.presentation.screen.confirmDialog.ConfirmDialog
import pw.vintr.vintrless.presentation.screen.confirmDialog.ConfirmDialogData
import pw.vintr.vintrless.presentation.screen.log.filter.LogFilterDialog
import pw.vintr.vintrless.presentation.screen.log.viewer.LogViewerScreen
import pw.vintr.vintrless.presentation.screen.main.MainScreen
import pw.vintr.vintrless.presentation.screen.profile.createNew.CreateNewProfileDialog
import pw.vintr.vintrless.presentation.screen.profile.editForm.EditProfileFormScreen
import pw.vintr.vintrless.presentation.screen.profile.list.ProfileListScreen
import pw.vintr.vintrless.presentation.screen.profile.scanQr.ScanProfileQrScreen
import pw.vintr.vintrless.presentation.screen.profile.share.ShareProfileDialog
import pw.vintr.vintrless.presentation.screen.routing.addressRecords.addRecords.AddAddressRecordsDialog
import pw.vintr.vintrless.presentation.screen.routing.addressRecords.editRecords.EditAddressRecordsScreen
import pw.vintr.vintrless.presentation.screen.routing.addressRecords.manualInputRecords.ManualInputAddressRecordsDialog
import pw.vintr.vintrless.presentation.screen.routing.rulesetList.RulesetListScreen
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.VintrlessTheme
import pw.vintr.vintrless.presentation.uikit.alert.Alert
import pw.vintr.vintrless.tools.extensions.extendedDialog
import pw.vintr.vintrless.tools.modules.appModule
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.profile_delete_text
import vintrless.composeapp.generated.resources.profile_delete_title
import vintrless.composeapp.generated.resources.apps_filter_process_delete_text
import vintrless.composeapp.generated.resources.apps_filter_process_delete_title
import vintrless.composeapp.generated.resources.common_delete

private const val TRANSITION_DURATION = 300

private const val ALERT_SHOW_DURATION = 2500L

@Composable
fun App() {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)

    VintrlessTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ModalBottomSheetLayout(
                bottomSheetNavigator = bottomSheetNavigator,
                sheetContentColor = Color.Transparent,
                sheetBackgroundColor = Color.Transparent,
                scrimColor = MaterialTheme.colorScheme.background
                    .copy(alpha = 0.9f),
                sheetElevation = 0.dp
            ) {
                Navigation(
                    navController = navController,
                    rootScreen = AppScreen.Main,
                )
            }
            AlertHolder()
        }
    }
}

@Composable
private fun AlertHolder() {
    val alertInteractor: AlertInteractor = koinInject()
    val alertState = alertInteractor.alertState.collectAsState(initial = AlertState())

    LaunchedEffect(key1 = alertState.value) {
        if (alertState.value.alertVisible) {
            delay(ALERT_SHOW_DURATION)
            alertInteractor.hideAlert()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        AnimatedVisibility(
            visible = alertState.value.alert != null && alertState.value.show,
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit =  shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            Alert(
                title = alertState.value.alert?.titleRes
                    ?.let { stringResource(it) }
                    .orEmpty(),
                message = alertState.value.alert?.messageRes
                    ?.let { stringResource(it) }
                    .orEmpty(),
                backgroundColor = when (alertState.value.alert?.type) {
                    POSITIVE -> VintrlessExtendedTheme.colors.positive
                    NEGATIVE -> VintrlessExtendedTheme.colors.negative
                    null -> VintrlessExtendedTheme.colors.cardBackgroundColor
                },
                onCloseAction = { alertInteractor.hideAlert() }
            )
        }
    }
}

@Composable
fun Navigation(
    navigator: AppNavigator = koinInject(),
    navController: NavHostController,
    rootScreen: Screen
) {
    NavigatorEffect(
        type = NavigatorType.Root,
        navigator = navigator,
        controller = navController,
    )

    NavHost(
        navController = navController,
        startDestination = rootScreen,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_DURATION)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_DURATION)) },
    ) {
        composable<AppScreen.Main> {
            MainScreen()
        }

        extendedDialog<AppScreen.CreateNewProfile> { CreateNewProfileDialog() }

        composable<AppScreen.ScanProfileQR> {
            ScanProfileQrScreen()
        }

        composable<AppScreen.EditProfileForm> {
            val route: AppScreen.EditProfileForm = it.toRoute()

            EditProfileFormScreen(
                protocolType = route.protocolType,
                dataId = route.dataId,
            )
        }

        composable<AppScreen.ProfileList> { ProfileListScreen() }

        extendedDialog<AppScreen.ShareProfile> {
            val route: AppScreen.ShareProfile = it.toRoute()

            ShareProfileDialog(dataId = route.dataId)
        }

        extendedDialog<AppScreen.ConfirmDeleteProfile> {
            ConfirmDialog(
                data = ConfirmDialogData.Resource(
                    titleRes = Res.string.profile_delete_title,
                    messageRes = Res.string.profile_delete_text,
                    acceptTextRes = Res.string.common_delete,
                )
            )
        }

        composable<AppScreen.RulesetList> { RulesetListScreen() }

        composable<AppScreen.EditAddressRecords> {
            val route: AppScreen.EditAddressRecords = it.toRoute()

            EditAddressRecordsScreen(
                rulesetId = route.rulesetId,
            )
        }

        extendedDialog<AppScreen.AddAddressRecords> {
            AddAddressRecordsDialog()
        }

        extendedDialog<AppScreen.ManualInputAddressRecords> {
            val route: AppScreen.ManualInputAddressRecords = it.toRoute()

            ManualInputAddressRecordsDialog(
                defaultReplaceCurrent = route.defaultReplaceCurrent,
            )
        }

        composable<AppScreen.AboutApp> { AboutAppScreen() }

        composable<AppScreen.ApplicationFilter> { ApplicationFilterScreen() }

        extendedDialog<AppScreen.ConfirmDeleteSystemProcess> {
            ConfirmDialog(
                data = ConfirmDialogData.Resource(
                    titleRes = Res.string.apps_filter_process_delete_title,
                    messageRes = Res.string.apps_filter_process_delete_text,
                    acceptTextRes = Res.string.common_delete,
                )
            )
        }

        composable<AppScreen.LogViewer> { LogViewerScreen() }

        extendedDialog<AppScreen.LogFilter> {
            val route: AppScreen.LogFilter = it.toRoute()

            LogFilterDialog(
                logFilter = LogFilter(
                    query = route.query,
                    selection = route.selection
                )
            )
        }
    }
}

@Composable
fun rememberBottomSheetNavigator(
    animationSpec: AnimationSpec<Float> = SpringSpec(),
    skipHalfExpanded: Boolean = false,
): BottomSheetNavigator {
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec,
        skipHalfExpanded = skipHalfExpanded
    )
    return remember(sheetState) {
        getBottomSheetNavigator(sheetState = sheetState)
    }
}
