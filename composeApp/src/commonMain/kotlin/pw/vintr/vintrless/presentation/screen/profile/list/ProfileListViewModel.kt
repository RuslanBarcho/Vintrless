package pw.vintr.vintrless.presentation.screen.profile.list

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayConnectionInteractor
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.screen.confirmDialog.ConfirmResult

class ProfileListViewModel(
    navigator: AppNavigator,
    private val profileInteractor: ProfileInteractor,
    private val v2RayConnectionInteractor: V2RayConnectionInteractor,
) : BaseViewModel(navigator) {

    val screenState = combine(
        profileInteractor.profileFlow,
        profileInteractor.selectedProfileFlow,
    ) { profiles, selectedProfile ->
        BaseScreenState.Loaded(
            ProfileListState(
                profiles = profiles,
                selectedProfile = selectedProfile
            )
        )
    }.stateInThis(BaseScreenState.Loading())

    fun selectProfile(profile: ProfileData) {
        launch {
            profileInteractor.setSelectedProfile(profile.id)
            v2RayConnectionInteractor.sendRestartCommand()
        }
    }

    fun openCreateNewProfile() {
        navigator.forward(AppScreen.CreateNewProfile)
    }

    fun openEditProfile(profile: ProfileData) {
        navigator.forward(
            AppScreen.EditProfileForm(
                profileTypeOrdinal = profile.type.ordinal,
                dataId = profile.id
            )
        )
    }

    fun onShareClick(profile: ProfileData) {
        navigator.forward(
            AppScreen.ShareProfile(dataId = profile.id)
        )
    }

    fun onDeleteClick(profile: ProfileData) {
        navigator.forwardWithResult<ConfirmResult>(
            screen = AppScreen.ConfirmDeleteProfile,
            type = NavigatorType.Root,
            resultKey = ConfirmResult.KEY,
        ) {
            if (it == ConfirmResult.ACCEPT) {
                launch { profileInteractor.removeProfile(profile.id) }
            }
        }
    }
}

data class ProfileListState(
    val profiles: List<ProfileData>,
    val selectedProfile: ProfileData?,
)
