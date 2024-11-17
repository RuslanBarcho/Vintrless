package pw.vintr.vintrless.presentation.screen.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.presentation.navigation.*
import pw.vintr.vintrless.presentation.screen.home.HomeScreen
import pw.vintr.vintrless.presentation.screen.settings.SettingsScreen
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.navbar.AppNavBarItem
import pw.vintr.vintrless.presentation.uikit.navbar.AppNavigationBar
import pw.vintr.vintrless.presentation.uikit.navbar.NAV_BAR_PADDING_DP

private const val TRANSITION_DURATION = 300

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VintrlessExtendedTheme.colors.screenBackgroundColor),
    ) {
        val navController = rememberNavController()
        val tabs = viewModel.bottomTabs.collectAsState()

        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = Tab.Home.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            tabs.value.forEach { tab ->
                composable(tab.route) {
                    TabNavigation(
                        modifier = Modifier.fillMaxSize(),
                        rootScreen = tab.rootScreen,
                        navigatorType = tab.navigatorType,
                    )
                }
            }
        }

        AppNavigationBar(
            modifier = Modifier
                .padding(bottom = NAV_BAR_PADDING_DP.dp)
                .align(Alignment.BottomCenter)
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            tabs.value.forEach { tab ->
                val isSelected = currentDestination?.hierarchy
                    ?.any { it.route == tab.route } == true

                AppNavBarItem(
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxHeight(),
                    selected = isSelected,
                    icon = tab.iconRes,
                    onClick = {
                        if (isSelected) {
                            viewModel.backToTabStart(tab.navigatorType)
                        } else {
                            navController.openTab(tab)
                            viewModel.setNavigatorType(tab.navigatorType)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TabNavigation(
    modifier: Modifier = Modifier,
    rootScreen: Screen,
    navigatorType: NavigatorType,
    navigator: AppNavigator = koinInject(),
) {
    val navController = rememberNavController()

    NavigatorEffect(
        type = navigatorType,
        navigator = navigator,
        controller = navController,
    )

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = rootScreen,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_DURATION)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_DURATION)) }
    ) {
        composable<AppScreen.Home> {
            HomeScreen()
        }
        composable<AppScreen.Settings> {
            SettingsScreen()
        }
    }
}

private fun NavController.openTab(tab: Tab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }

        launchSingleTop = true
        restoreState = true
    }
}
