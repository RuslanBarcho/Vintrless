package pw.vintr.vintrless.presentation.navigation

import kotlinx.serialization.Serializable
import pw.vintr.vintrless.domain.profile.model.ProfileType

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
    data class EditProfileForm(
        val profileTypeOrdinal: Int,
        val dataId: String? = null,
    ) : AppScreen() {

        val profileType: ProfileType
            get() = ProfileType.entries[profileTypeOrdinal]
    }

    @Serializable
    data object ProfileList : AppScreen()

    @Serializable
    data object ConfirmDeleteProfile : AppScreen()
}
