package pw.vintr.vintrless.presentation.screen.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.compose.resources.DrawableResource
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.navigation.Screen
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_home
import vintrless.composeapp.generated.resources.ic_settings

sealed interface TabNavigator : NavigatorType {
    data object Home : TabNavigator
    data object Settings : TabNavigator
}

sealed class Tab(
    val route: String,
    val iconRes: DrawableResource,
    val navigatorType: NavigatorType,
    val rootScreen: Screen,
) {
    data object Home : Tab(
        route = "home",
        iconRes = Res.drawable.ic_home,
        navigatorType = TabNavigator.Home,
        rootScreen = AppScreen.Home
    )
    data object Settings : Tab(
        route = "setting",
        iconRes = Res.drawable.ic_settings,
        navigatorType = TabNavigator.Settings,
        rootScreen = AppScreen.Settings
    )
}

val tabs = listOf(
    Tab.Home,
    Tab.Settings
)

class MainViewModel(
    navigator: AppNavigator
) : BaseViewModel(navigator) {

    init {
        setNavigatorType(TabNavigator.Home)
    }

    val bottomTabs = MutableStateFlow(tabs).asStateFlow()

    fun setNavigatorType(navigatorType: NavigatorType) {
        navigator.switchNavigatorType(navigatorType)
    }

    fun backToTabStart(navigatorType: NavigatorType) {
        navigator.backToStart(navigatorType)
    }

    fun restoreNavigatorType(route: String) {
        tabs
            .find { it.route == route }
            ?.let { setNavigatorType(it.navigatorType) }
    }
}
