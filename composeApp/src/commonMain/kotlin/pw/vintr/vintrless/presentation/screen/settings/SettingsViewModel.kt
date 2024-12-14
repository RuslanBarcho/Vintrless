package pw.vintr.vintrless.presentation.screen.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.compose.resources.StringResource
import pw.vintr.vintrless.platform.AppConfig
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.tools.extensions.Dot
import pw.vintr.vintrless.tools.extensions.Space
import vintrless.composeapp.generated.resources.*

class SettingsViewModel(
    navigator: AppNavigator
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(SettingsState(
        items = listOf(
            SettingsItem.Routing(),
            SettingsItem.About(
                appVersionName = AppConfig.appVersionName,
                v2RayCoreVersionName = AppConfig.v2RayCoreVersionName
            ),
        ),
    ))
    val screenState = _screenState.asStateFlow()

    fun onSettingItemClick(item: SettingsItem) {
        when (item) {
            is SettingsItem.Routing -> {
                navigator.switchNavigatorType(NavigatorType.Root)
                navigator.forward(AppScreen.RulesetList)
            }
            else -> Unit
        }
    }
}

data class SettingsState(
    val items: List<SettingsItem>,
)

sealed class SettingsItem {

    abstract val titleRes: StringResource

    open val descriptionRes: StringResource? = null

    open val descriptionText: String? = null

    open val descriptionArgs: List<Any> = listOf()

    data class App(
        override val titleRes: StringResource = Res.string.settings_app_title,
        override val descriptionRes: StringResource = Res.string.settings_app_description
    ) : SettingsItem()

    data class Vpn(
        override val titleRes: StringResource = Res.string.settings_vpn_title,
        override val descriptionRes: StringResource = Res.string.settings_vpn_description
    ) : SettingsItem()

    data class Routing(
        override val titleRes: StringResource = Res.string.settings_routing_title,
        override val descriptionRes: StringResource = Res.string.settings_routing_description
    ) : SettingsItem()

    data class About(
        override val titleRes: StringResource = Res.string.settings_about_title,
        val appVersionName: String,
        val v2RayCoreVersionName: String,
    ) : SettingsItem() {

        override val descriptionText = buildString {
            append(appVersionName)
            append(String.Space)
            append(String.Dot)
            append(String.Space)
            append(v2RayCoreVersionName)
        }
    }
}
