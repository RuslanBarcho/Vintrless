package pw.vintr.vintrless.presentation.screen.home

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.connection.model.ConnectionState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen
import pw.vintr.vintrless.presentation.navigation.NavigatorType

class HomeViewModel(
    navigator: AppNavigator
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(HomeScreenState(ConnectionState.Disconnected))
    val screenState = _screenState.asStateFlow()

    fun toggle() {
        when (_screenState.value.connectionState) {
            ConnectionState.Disconnected -> connect()
            ConnectionState.Connecting,
            ConnectionState.Connected -> disconnect()
        }
    }

    private fun connect() {
        launch {
            _screenState.update { it.copy(connectionState = ConnectionState.Connecting) }
            delay(1000)
            _screenState.update { it.copy(connectionState = ConnectionState.Connected) }
        }
    }

    private fun disconnect() {
        _screenState.update { it.copy(connectionState = ConnectionState.Disconnected) }
    }

    fun openCreateNewProfile() {
        navigator.switchNavigatorType(NavigatorType.Root)
        navigator.forward(AppScreen.CreateNewProfile)
    }
}

data class HomeScreenState(
    val connectionState: ConnectionState,
)
