package pw.vintr.vintrless.presentation.screen.about

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pw.vintr.vintrless.platform.AppConfig
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.Dot
import pw.vintr.vintrless.tools.extensions.Space

class AboutAppViewModel(
    navigator: AppNavigator,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(
        AboutAppState(
            appVersion = buildString {
                append(AppConfig.appVersionName)
                append(String.Space)
                append(String.Dot)
                append(String.Space)
                append(AppConfig.v2RayCoreVersionName)
            }
        )
    )
    val screenState = _screenState.asStateFlow()
}

data class AboutAppState(
    val appVersion: String,
)
