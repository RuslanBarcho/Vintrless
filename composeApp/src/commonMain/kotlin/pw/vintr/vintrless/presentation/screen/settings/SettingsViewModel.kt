package pw.vintr.vintrless.presentation.screen.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.compose.resources.StringResource
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import vintrless.composeapp.generated.resources.*

class SettingsViewModel(
    navigator: AppNavigator
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(SettingsState(
        items = listOf(
            SettingsItem.App(),
            SettingsItem.Vpn(),
            SettingsItem.Proxy(),
            SettingsItem.Routing(),
            SettingsItem.About(
                appVersion = "1.0",
                xrayCoreVersion = "24.10.16"
            ),
        ),
    ))
    val screenState = _screenState.asStateFlow()
}

data class SettingsState(
    val items: List<SettingsItem>,
)

sealed class SettingsItem {

    abstract val titleRes: StringResource

    abstract val descriptionRes: StringResource

    open val descriptionArgs: List<Any> = listOf()

    data class App(
        override val titleRes: StringResource = Res.string.settings_app_title,
        override val descriptionRes: StringResource = Res.string.settings_app_description
    ) : SettingsItem()

    data class Vpn(
        override val titleRes: StringResource = Res.string.settings_vpn_title,
        override val descriptionRes: StringResource = Res.string.settings_vpn_description
    ) : SettingsItem()

    data class Proxy(
        override val titleRes: StringResource = Res.string.settings_proxy_title,
        override val descriptionRes: StringResource = Res.string.settings_proxy_description
    ) : SettingsItem()

    data class Routing(
        override val titleRes: StringResource = Res.string.settings_routing_title,
        override val descriptionRes: StringResource = Res.string.settings_routing_description
    ) : SettingsItem()

    data class About(
        override val titleRes: StringResource = Res.string.settings_about_title,
        override val descriptionRes: StringResource = Res.string.settings_about_description,
        val appVersion: String,
        val xrayCoreVersion: String,
    ) : SettingsItem() {

        override val descriptionArgs: List<Any> = listOf(appVersion, xrayCoreVersion)
    }
}
