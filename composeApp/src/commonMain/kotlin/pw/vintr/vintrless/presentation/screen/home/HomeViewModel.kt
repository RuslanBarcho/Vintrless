package pw.vintr.vintrless.presentation.screen.home

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.alert.interactor.AlertInteractor
import pw.vintr.vintrless.domain.alert.model.AlertModel
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayConnectionInteractor
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.presentation.navigation.NavigatorType

class HomeViewModel(
    navigator: AppNavigator,
    private val alertInteractor: AlertInteractor,
    private val profileInteractor: ProfileInteractor,
    private val v2RayConnectionInteractor: V2RayConnectionInteractor
) : BaseViewModel(navigator) {

    private val connectionState = v2RayConnectionInteractor.connectionState
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

    init {
        subscribeV2RayConnectionEvents()
    }

    private fun subscribeV2RayConnectionEvents() = launch {
        v2RayConnectionInteractor.event.collectLatest { event ->
            when (event) {
                is V2RayConnectionInteractor.Event.ShowWrongSudoPasswordError -> {
                    alertInteractor.showAlert(AlertModel.WrongSudoPassword())
                }
                else -> Unit
            }
        }
    }

    fun toggle() {
        when (connectionState.value) {
            ConnectionState.Disconnected -> connect()
            ConnectionState.Connecting,
            ConnectionState.Connected -> disconnect()
        }
    }

    private fun connect() {
        launch {
            if (profileInteractor.getSelectedProfile() != null) {
                v2RayConnectionInteractor.sendStartCommand()
            } else {
                openCreateNewProfile()
            }
        }
    }

    private fun disconnect() {
        v2RayConnectionInteractor.sendStopCommand()
    }

    fun openCreateNewProfile() {
        navigator.switchNavigatorType(NavigatorType.Root)
        navigator.forward(AppScreen.CreateNewProfile)
    }

    fun openProfileList() {
        navigator.switchNavigatorType(NavigatorType.Root)
        navigator.forward(AppScreen.ProfileList)
    }

    fun openLogs() {
        navigator.switchNavigatorType(NavigatorType.Root)
        navigator.forward(AppScreen.LogViewer)
    }
}

data class HomeScreenState(
    val connectionState: ConnectionState,
    val selectedProfile: ProfileData?,
    val hasProfiles: Boolean,
)
