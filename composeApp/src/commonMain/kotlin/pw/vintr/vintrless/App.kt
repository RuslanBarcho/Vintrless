package pw.vintr.vintrless

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import pw.vintr.vintrless.presentation.navigation.*
import pw.vintr.vintrless.presentation.screen.main.MainScreen
import pw.vintr.vintrless.presentation.screen.profile.createNew.CreateNewProfileDialog
import pw.vintr.vintrless.presentation.screen.profile.editForm.EditProfileFormScreen
import pw.vintr.vintrless.presentation.theme.VintrlessTheme
import pw.vintr.vintrless.tools.extensions.extendedDialog
import pw.vintr.vintrless.tools.modules.appModule

private const val TRANSITION_DURATION = 300

@Composable
fun App() {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)

    VintrlessTheme {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            KoinApplication(application = {
                modules(appModule)
            }) {
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
            }
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
        extendedDialog<AppScreen.CreateNewProfile>(
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false,
            )
        ) { CreateNewProfileDialog() }
        composable<AppScreen.EditProfileForm> {
            val route: AppScreen.EditProfileForm = it.toRoute()

            EditProfileFormScreen(
                profileType = route.profileType,
                dataId = route.dataId,
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
