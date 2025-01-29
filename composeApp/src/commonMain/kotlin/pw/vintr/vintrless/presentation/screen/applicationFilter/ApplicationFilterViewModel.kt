package pw.vintr.vintrless.presentation.screen.applicationFilter

import androidx.compose.runtime.Stable
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterMode
import pw.vintr.vintrless.domain.userApplications.interactor.UserApplicationsInteractor
import pw.vintr.vintrless.domain.userApplications.model.common.IDeviceApplication
import pw.vintr.vintrless.domain.userApplications.model.common.process.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.common.application.UserApplication
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilter
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayConnectionInteractor
import pw.vintr.vintrless.platform.model.PlatformType
import pw.vintr.vintrless.platformType
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ApplicationFilterViewModel(
    navigator: AppNavigator,
    private val userApplicationsInteractor: UserApplicationsInteractor,
    private val v2RayConnectionInteractor: V2RayConnectionInteractor,
) : BaseViewModel(navigator) {

    companion object {
        private const val PROCESS_FETCH_DELAY = 10000
    }

    private val _screenState = MutableStateFlow<BaseScreenState<ApplicationFilterScreenState>>(
        BaseScreenState.Loading()
    )
    val screenState = _screenState.asStateFlow()

    private var processFetchJob: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        _screenState.loadWithStateHandling {
            // Running system processes
            val processes = async { userApplicationsInteractor.getRunningProcesses() }

            // User applications + manual saved system processes
            val applications = async { userApplicationsInteractor.getUserApplications() }
            val savedProcesses = async { userApplicationsInteractor.getSavedProcesses() }

            // Filter value
            val enabled = async { userApplicationsInteractor.getFilterEnabled() }
            val filterValue = async { userApplicationsInteractor.getFilter() }

            ApplicationFilterScreenState(
                enabled = enabled.await(),
                userInstalledApplications = applications.await(),
                savedSystemProcesses = savedProcesses.await(),
                processAddFormState = ProcessAddFormState(
                    enabled = platformType() == PlatformType.JVM,
                    runningProcessesState = RunningProcessesState(
                        processes = processes.await(),
                        fetchTime = Clock.System.now().toEpochMilliseconds(),
                    ),
                ),
                filterState = ApplicationFilterState(
                    savedFilter = filterValue.await(),
                    pickedFilter = filterValue.await().copy(),
                    isSaving = false,
                )
            )
        }
    }

    fun setEnabled(value: Boolean) {
        launch(createExceptionHandler()) {
            // Save & update screen state
            userApplicationsInteractor.saveFilterEnabled(value)
            _screenState.updateLoaded { it.copy(enabled = value) }

            // Apply filter to running V2Ray process
            v2RayConnectionInteractor.sendRestartCommand()
        }
    }

    fun setFilterMode(value: ApplicationFilterMode) {
        _screenState.updateLoaded { state ->
            state.copy(
                filterState = state.filterState.copy(
                    pickedFilter = state.filterState.pickedFilter.copy(
                        mode = value
                    )
                )
            )
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

    @OptIn(ExperimentalUuidApi::class)
    fun saveProcess() {
        _screenState.getWithLoaded { state ->
            SystemProcess(
                id = Uuid.random().toString(),
                appName = state.processAddFormState.appNameValue,
                processName = state.processAddFormState.processNameValue,
            )
        }?.let { process ->
            launch {
                userApplicationsInteractor.saveProcess(process)
                _screenState.updateLoaded { state ->
                    state.copy(
                        savedSystemProcesses = state.savedSystemProcesses
                            .toMutableList()
                            .apply { add(0, process) },
                        processAddFormState = state.processAddFormState.copy(
                            appNameValue = String.Empty,
                            processNameValue = String.Empty,
                        )
                    )
                }
            }
        }
    }

    private fun fetchProcesses() {
        processFetchJob.cancelIfActive()
        processFetchJob = launch(createExceptionHandler()) {
            _screenState.withLoaded { state ->
                val currentTime = Clock.System.now().toEpochMilliseconds()
                val lastFetchTime = state.processAddFormState.runningProcessesState.fetchTime

                if (
                    currentTime - lastFetchTime >= PROCESS_FETCH_DELAY ||
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

    fun toggleApplicationSelected(application: IDeviceApplication) {
        _screenState.updateLoaded { state ->
            val currentKeys = state.filterState.pickedFilter.filterKeys
            val modifiedKeys = if (currentKeys.contains(application.processName)) {
                currentKeys.toMutableSet().apply { remove(application.processName) }
            } else {
                currentKeys.toMutableSet().apply { add(application.processName) }
            }

            state.copy(
                filterState = state.filterState.copy(
                    pickedFilter = state.filterState.pickedFilter.copy(
                        filterKeys = modifiedKeys
                    )
                )
            )
        }
    }

    fun saveFilter() {
        launch(createExceptionHandler()) {
            _screenState.withLoaded { state ->
                withLoading(
                    setLoadingCallback = { loading ->
                        _screenState.updateLoaded { state ->
                            state.copy(
                                filterState = state.filterState.copy(
                                    isSaving = loading,
                                )
                            )
                        }
                    },
                    action = {
                        // Save filter
                        val filterToSave = state.filterState.pickedFilter
                        userApplicationsInteractor.saveFilter(filterToSave)

                        // Put saved filter to saved one
                        _screenState.updateLoaded { state ->
                            state.copy(
                                filterState = state.filterState.copy(
                                    savedFilter = filterToSave.copy()
                                )
                            )
                        }

                        // Apply filter to running V2Ray process
                        v2RayConnectionInteractor.sendRestartCommand()
                    }
                )
            }
        }
    }
}

@Stable
data class ApplicationFilterScreenState(
    val enabled: Boolean = false,
    val availableFilterModes: List<ApplicationFilterMode> = listOf(
        ApplicationFilterMode.BLACKLIST,
        ApplicationFilterMode.WHITELIST,
    ),
    val userInstalledApplications: List<UserApplication> = listOf(),
    val savedSystemProcesses: List<SystemProcess> = listOf(),
    val processAddFormState: ProcessAddFormState = ProcessAddFormState(
        runningProcessesState = RunningProcessesState()
    ),
    val searchValue: String = String.Empty,
    val filterState: ApplicationFilterState,
) {
    val filteredUserInstalledApplications = if (searchValue.isNotEmpty()) {
        userInstalledApplications.filter { it.name.contains(searchValue, ignoreCase = true) }
    } else {
        userInstalledApplications
    }

    val filteredSavedSystemProcesses = if (searchValue.isNotEmpty()) {
        savedSystemProcesses.filter { it.appName.contains(searchValue, ignoreCase = true) }
    } else {
        savedSystemProcesses
    }

    val canBeSaved: Boolean = filterState.canBeSaved
}

@Stable
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

@Stable
data class RunningProcessesState(
    val processes: List<SystemProcess> = listOf(),
    val fetchTime: Long = 0,
)

@Stable
data class ApplicationFilterState(
    val savedFilter: ApplicationFilter,
    val pickedFilter: ApplicationFilter,
    val isSaving: Boolean,
) {

    val canBeSaved: Boolean = savedFilter != pickedFilter
}
