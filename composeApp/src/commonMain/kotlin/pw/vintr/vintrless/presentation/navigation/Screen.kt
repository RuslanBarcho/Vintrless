package pw.vintr.vintrless.presentation.navigation

import kotlinx.serialization.Serializable
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType

interface Screen

@Serializable
sealed class AppScreen : Screen {

    @Serializable
    data object Main : AppScreen()

    @Serializable
    data object Home : AppScreen()

    @Serializable
    data object Settings : AppScreen()

    @Serializable
    data object CreateNewProfile : AppScreen()

    @Serializable
    data object ScanProfileQR : AppScreen()

    @Serializable
    data class EditProfileForm(
        val profileTypeOrdinal: Int,
        val dataId: String? = null,
    ) : AppScreen() {

        val protocolType: ProtocolType
            get() = ProtocolType.entries[profileTypeOrdinal]
    }

    @Serializable
    data object ProfileList : AppScreen()

    @Serializable
    data class ShareProfile(val dataId: String) : AppScreen()

    @Serializable
    data object ConfirmDeleteProfile : AppScreen()

    @Serializable
    data object RulesetList : AppScreen()

    @Serializable
    data class EditAddressRecords(
        val rulesetId: String,
    ) : AppScreen()

    @Serializable
    data object AddAddressRecords : AppScreen()

    @Serializable
    data class ManualInputAddressRecords(
        val defaultReplaceCurrent: Boolean,
    ) : AppScreen()

    @Serializable
    data object AboutApp : AppScreen()

    @Serializable
    data object ApplicationFilter : AppScreen()

    @Serializable
    data object ConfirmDeleteSystemProcess : AppScreen()
}
