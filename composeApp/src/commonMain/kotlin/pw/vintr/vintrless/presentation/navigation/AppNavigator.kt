package pw.vintr.vintrless.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pw.vintr.vintrless.presentation.navigation.navResult.ResultListenerHandler
import pw.vintr.vintrless.presentation.navigation.navResult.ResultWire

private const val NAVIGATION_EFFECT_KEY = "navigation"

class AppNavigator {

    private val _actionFlow = MutableSharedFlow<NavigatorAction>(extraBufferCapacity = 10)

    private val resultWire = ResultWire()

    private var currentNavigatorType: NavigatorType = NavigatorType.Root

    val actionFlow = _actionFlow.asSharedFlow()

    fun switchNavigatorType(type: NavigatorType) {
        currentNavigatorType = type
    }

    fun back(type: NavigatorType? = null) {
        _actionFlow.tryEmit(
            NavigatorAction.Back(
                navigatorType = type ?: currentNavigatorType
            )
        )
    }

    fun <T: Any> back(type: NavigatorType? = null, resultKey: String, result: T? = null) {
        _actionFlow.tryEmit(
            NavigatorAction.Back(
                navigatorType = type ?: currentNavigatorType
            )
        )

        result
            ?.let { resultWire.sendResult(resultKey, it) }
            ?: run { resultWire.removeListener(resultKey) }
    }

    fun backToStart(type: NavigatorType? = null) {
        _actionFlow.tryEmit(
            NavigatorAction.BackToStart(
                navigatorType = type ?: currentNavigatorType
            )
        )
    }

    fun forward(
        screen: Screen,
        type: NavigatorType? = null
    ) {
        _actionFlow.tryEmit(
            NavigatorAction.Forward(
                screen = screen,
                navigatorType = type ?: currentNavigatorType,
            )
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> forwardWithResult(
        screen: Screen,
        type: NavigatorType? = null,
        resultKey: String,
        resultCallback: (T) -> Unit
    ): ResultListenerHandler {
        _actionFlow.tryEmit(
            NavigatorAction.Forward(
                screen = screen,
                navigatorType = type ?: currentNavigatorType,
            )
        )

        return resultWire.setResultListener(resultKey) { resultCallback(it as T) }
    }

    fun replaceAll(
        screen: Screen,
        type: NavigatorType? = null,
        applyNavigatorType: Boolean = false
    ) {
        _actionFlow.tryEmit(
            NavigatorAction.ReplaceAll(
                screen = screen,
                navigatorType = type ?: currentNavigatorType,
            )
        )

        if (applyNavigatorType && type != null) {
            currentNavigatorType = type
        }
    }
}

sealed class NavigatorAction {

    abstract val navigatorType: NavigatorType

    data class Back(
        override val navigatorType: NavigatorType
    ) : NavigatorAction()

    data class BackToStart(
        override val navigatorType: NavigatorType
    ) : NavigatorAction()

    data class Forward(
        val screen: Screen,
        override val navigatorType: NavigatorType,
    ) : NavigatorAction()

    data class ReplaceAll(
        val screen: Screen,
        override val navigatorType: NavigatorType,
    ) : NavigatorAction()
}

interface NavigatorType {
    object Root : NavigatorType
}

@Composable
fun NavigatorEffect(
    type: NavigatorType,
    navigator: AppNavigator,
    controller: NavController,
    onCustomCommand: (NavigatorAction) -> Unit = {},
) {
    LaunchedEffect(NAVIGATION_EFFECT_KEY) {
        navigator.actionFlow.onEach { action ->
            if (action.navigatorType == type) {
                when (action) {
                    is NavigatorAction.Back -> {
                        controller.navigateUp()
                    }
                    is NavigatorAction.BackToStart -> {
                        controller.graph.findStartDestination().route?.let {
                            controller.popBackStack(it, inclusive = false)
                        }
                    }
                    is NavigatorAction.Forward -> {
                        controller.navigate(action.screen)
                    }
                    is NavigatorAction.ReplaceAll -> {
                        controller.navigate(action.screen) { popUpToTop(controller) }
                    }
                    else -> {
                        onCustomCommand(action)
                    }
                }
            }
        }.launchIn(scope = this)
    }
}

fun NavOptionsBuilder.popUpToTop(navController: NavController) {
    popUpTo(navController.graph.id) {
        inclusive =  true
    }
}
