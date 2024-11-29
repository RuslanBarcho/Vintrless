package pw.vintr.vintrless.presentation.screen.home

import kotlinx.coroutines.flow.combine
import pw.vintr.vintrless.V2rayInteractor
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.v2ray.interactor.V2rayInteractor
import pw.vintr.vintrless.domain.v2ray.model.V2rayConfig
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.presentation.navigation.NavigatorType

class HomeViewModel(
    navigator: AppNavigator,
    profileInteractor: ProfileInteractor,
    private val v2rayInteractor: V2rayInteractor = V2rayInteractor()
) : BaseViewModel(navigator) {

    private val connectionState = v2rayInteractor.connectionState
        .stateInThis(ConnectionState.Disconnected)

    val screenState = combine(
        connectionState,
        profileInteractor.profileFlow,
        profileInteractor.selectedProfileFlow,
    ) { connection, profiles, selectedProfile ->
        BaseScreenState.Loaded(
            HomeScreenState(
                connectionState = connection,
                selectedProfile = selectedProfile,
                hasProfiles = profiles.isNotEmpty(),
            )
        )
    }.stateInThis(initialValue = BaseScreenState.Loading())

    fun toggle() {
        when (connectionState.value) {
            ConnectionState.Disconnected -> connect()
            ConnectionState.Connecting,
            ConnectionState.Connected -> disconnect()
        }
    }

    private fun connect() {
        v2rayInteractor.startV2ray(V2rayConfig())
    }

    private fun disconnect() {
        v2rayInteractor.stopV2ray()
    }

    fun openCreateNewProfile() {
        navigator.switchNavigatorType(NavigatorType.Root)
        navigator.forward(AppScreen.CreateNewProfile)
    }

    fun openProfileList() {
        navigator.switchNavigatorType(NavigatorType.Root)
        navigator.forward(AppScreen.ProfileList)
    }
}

data class HomeScreenState(
    val connectionState: ConnectionState,
    val selectedProfile: ProfileData?,
    val hasProfiles: Boolean,
)
