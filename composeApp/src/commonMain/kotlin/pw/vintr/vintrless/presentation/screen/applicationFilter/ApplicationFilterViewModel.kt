package pw.vintr.vintrless.presentation.screen.applicationFilter

import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import pw.vintr.vintrless.domain.applicationFilter.model.ApplicationFilterMode
import pw.vintr.vintrless.domain.userApplications.interactor.UserApplicationsInteractor
import pw.vintr.vintrless.domain.userApplications.model.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
import pw.vintr.vintrless.platform.model.PlatformType
import pw.vintr.vintrless.platformType
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.*

class ApplicationFilterViewModel(
    navigator: AppNavigator,
    private val userApplicationsInteractor: UserApplicationsInteractor,
) : BaseViewModel(navigator) {

    companion object {
        private const val PROCESS_FETCH_DELAY = 10000
    }

    private val _screenState = MutableStateFlow<BaseScreenState<ApplicationFilterState>>(BaseScreenState.Loading())
    val screenState = _screenState.asStateFlow()

    private var processFetchJob: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        _screenState.loadWithStateHandling {
            val applications = async { userApplicationsInteractor.getUserApplications() }
            val processes = async { userApplicationsInteractor.getRunningProcesses() }

            ApplicationFilterState(
                enabled = false,
                userInstalledApplications = applications.await(),
                manualAddedApplications = listOf(),
                selectedFilterMode = ApplicationFilterMode.BLACK_LIST,
                processAddFormState = ProcessAddFormState(
                    enabled = platformType() == PlatformType.JVM,
                    runningProcessesState = RunningProcessesState(
                        processes = processes.await(),
                        fetchTime = Clock.System.now().toEpochMilliseconds(),
                    ),
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

    fun setAddFormAppName(value: String) {
        _screenState.updateLoaded { state ->
            state.copy(
                processAddFormState = state.processAddFormState.copy(
                    appNameValue = value,
                )
            )
        }
        fetchProcesses()
    }

    fun setAddFormProcessName(value: String) {
        _screenState.updateLoaded { state ->
            state.copy(
                processAddFormState = state.processAddFormState.copy(
                    processNameValue = value,
                )
            )
        }
        fetchProcesses()
    }

    fun setAddFormValue(value: SystemProcess) {
        _screenState.updateLoaded { state ->
            state.copy(
                processAddFormState = state.processAddFormState.copy(
                    appNameValue = value.appName,
                    processNameValue = value.processName,
                )
            )
        }
    }

    private fun fetchProcesses() {
        processFetchJob.cancelIfActive()
        processFetchJob = launch(createExceptionHandler()) {
            _screenState.withLoaded { state ->
                val currentTime = Clock.System.now().toEpochMilliseconds()

                if (
                    currentTime - state.processAddFormState.runningProcessesState.fetchTime >= PROCESS_FETCH_DELAY ||
                    state.processAddFormState.runningProcessesState.processes.isEmpty()
                ) {
                    val newState = RunningProcessesState(
                        processes = userApplicationsInteractor.getRunningProcesses(),
                        fetchTime = Clock.System.now().toEpochMilliseconds(),
                    )

                    _screenState.value = BaseScreenState.Loaded(
                        payload = state.copy(
                            processAddFormState = state.processAddFormState.copy(
                                runningProcessesState = newState
                            )
                        )
                    )
                }
            }
        }
    }

    fun setSearchValue(value: String) {
        _screenState.updateLoaded { it.copy(searchValue = value) }
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
    val processAddFormState: ProcessAddFormState = ProcessAddFormState(
        runningProcessesState = RunningProcessesState()
    ),
    val searchValue: String = String.Empty,
) {
    val filteredUserInstalledApplications = if (searchValue.isNotEmpty()) {
        userInstalledApplications.filter { it.name.contains(searchValue, ignoreCase = true) }
    } else {
        userInstalledApplications
    }

    val filteredManualAddedApplications = if (searchValue.isNotEmpty()) {
        manualAddedApplications.filter { it.name.contains(searchValue, ignoreCase = true) }
    } else {
        manualAddedApplications
    }
}

data class ProcessAddFormState(
    val enabled: Boolean = false,
    val appNameValue: String = String.Empty,
    val processNameValue: String = String.Empty,
    val runningProcessesState: RunningProcessesState,
    val isSaving: Boolean = false,
) {
    val processesByAppName = if (appNameValue.isNotEmpty()) {
        runningProcessesState.processes.filter { it.appName.contains(appNameValue) }
    } else {
        runningProcessesState.processes
    }

    val processesByProcessName = if (processNameValue.isNotEmpty()) {
        runningProcessesState.processes.filter { it.processName.contains(processNameValue) }
    } else {
        runningProcessesState.processes
    }

    val formIsValid: Boolean = appNameValue.isNotEmpty() && processNameValue.isNotEmpty()
}

data class RunningProcessesState(
    val processes: List<SystemProcess> = listOf(),
    val fetchTime: Long = 0,
)
