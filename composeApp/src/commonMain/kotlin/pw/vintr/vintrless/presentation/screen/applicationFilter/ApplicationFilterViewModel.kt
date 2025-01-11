package pw.vintr.vintrless.presentation.screen.applicationFilter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pw.vintr.vintrless.domain.applicationFilter.model.ApplicationFilterMode
import pw.vintr.vintrless.domain.userApplications.interactor.UserApplicationsInteractor
import pw.vintr.vintrless.domain.userApplications.model.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
import pw.vintr.vintrless.platform.model.PlatformType
import pw.vintr.vintrless.platformType
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.Empty
import pw.vintr.vintrless.tools.extensions.updateLoaded

class ApplicationFilterViewModel(
    navigator: AppNavigator,
    private val userApplicationsInteractor: UserApplicationsInteractor,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow<BaseScreenState<ApplicationFilterState>>(BaseScreenState.Loading())
    val screenState = _screenState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _screenState.loadWithStateHandling {
            ApplicationFilterState(
                enabled = false,
                userInstalledApplications = userApplicationsInteractor.getUserApplications(),
                manualAddedApplications = listOf(),
                selectedFilterMode = ApplicationFilterMode.BLACK_LIST,
                processAddFormState = ProcessAddFormState(
                    enabled = platformType() == PlatformType.JVM,
                )
            )
        }
    }

    fun setEnabled(value: Boolean) {
        _screenState.updateLoaded { state ->
            state.copy(enabled = value)
        }
    }

    fun setFilterMode(value: ApplicationFilterMode) {
        _screenState.updateLoaded { state ->
            state.copy(selectedFilterMode = value)
        }
    }
}

data class ApplicationFilterState(
    val enabled: Boolean = false,
    val selectedFilterMode: ApplicationFilterMode,
    val availableFilterModes: List<ApplicationFilterMode> = listOf(
        ApplicationFilterMode.BLACK_LIST,
        ApplicationFilterMode.WHITE_LIST,
    ),
    val userInstalledApplications: List<UserApplication> = listOf(),
    val manualAddedApplications: List<UserApplication> = listOf(),
    val processAddFormState: ProcessAddFormState = ProcessAddFormState(),
)

data class ProcessAddFormState(
    val enabled: Boolean = false,
    val appNameValue: String = String.Empty,
    val processNameValue: String = String.Empty,
    val activeProcessTooltip: List<SystemProcess> = listOf(),
    val isSaving: Boolean = false,
) {

    val formIsValid: Boolean = appNameValue.isNotEmpty() && processNameValue.isNotEmpty()
}
