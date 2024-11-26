package pw.vintr.vintrless.presentation.screen.home

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.connection.model.ConnectionState
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.presentation.navigation.NavigatorType

class HomeViewModel(
    navigator: AppNavigator,
    profileInteractor: ProfileInteractor,
) : BaseViewModel(navigator) {

    private val connectionState = MutableStateFlow(ConnectionState.Disconnected)

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
        launch {
            connectionState.update { ConnectionState.Connecting }
            delay(1000)
            connectionState.update { ConnectionState.Connected }
        }
    }

    private fun disconnect() {
        connectionState.update { ConnectionState.Disconnected }
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
