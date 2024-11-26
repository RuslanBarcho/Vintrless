package pw.vintr.vintrless.presentation.screen.profile.list

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.presentation.navigation.NavigatorType

class ProfileListViewModel(
    navigator: AppNavigator,
    private val profileInteractor: ProfileInteractor,
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
        launch { profileInteractor.setSelectedProfile(profile.id) }
    }

    fun openCreateNewProfile() {
        navigator.switchNavigatorType(NavigatorType.Root)
        navigator.forward(AppScreen.CreateNewProfile)
    }
}

data class ProfileListState(
    val profiles: List<ProfileData>,
    val selectedProfile: ProfileData?,
)
