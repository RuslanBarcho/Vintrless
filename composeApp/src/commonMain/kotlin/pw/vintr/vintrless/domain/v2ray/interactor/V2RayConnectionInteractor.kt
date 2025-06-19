package pw.vintr.vintrless.domain.v2ray.interactor

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import pw.vintr.vintrless.V2RayPlatformInteractor
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.base.InteractorEvent
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.routing.interactor.RoutingInteractor
import pw.vintr.vintrless.domain.userApplications.interactor.UserApplicationsInteractor
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.v2ray.useCase.V2RayConfigBuildUseCase
import pw.vintr.vintrless.tools.extensions.throttleLatest

class V2RayConnectionInteractor(
    private val v2rayInteractor: V2RayPlatformInteractor = V2RayPlatformInteractor(),
    private val profileInteractor: ProfileInteractor,
    private val routingInteractor: RoutingInteractor,
    private val userApplicationInteractor: UserApplicationsInteractor,
) : BaseInteractor() {

    sealed class Event : InteractorEvent {
        data object ShowWrongSudoPasswordError : Event()
    }

    private sealed class Command {
        abstract val timestamp: Long

        data class Idle(
            override val timestamp: Long = Clock.System.now().toEpochMilliseconds()
        ) : Command()

        data class Start(
            override val timestamp: Long = Clock.System.now().toEpochMilliseconds()
        ) : Command()

        data class Restart(
            override val timestamp: Long = Clock.System.now().toEpochMilliseconds()
        ) : Command()

        data class Stop(
            override val timestamp: Long = Clock.System.now().toEpochMilliseconds()
        ) : Command()
    }

    private val commandBufferFlow: MutableStateFlow<Command> = MutableStateFlow(Command.Idle())

    private var commandBufferSubscriptionJob: Job? = null

    val connectionState: Flow<ConnectionState> = v2rayInteractor.connectionState

    override val event by lazy {
        merge(super.event, v2rayInteractor.event)
            .shareIn(this, started = SharingStarted.Lazily)
    }

    init {
        subscribeCommandBuffer()
    }

    private fun subscribeCommandBuffer() {
        commandBufferSubscriptionJob = launch {
            commandBufferFlow
                .throttleLatest(500)
                .collectLatest { processCommand(it) }
        }
    }

    private suspend fun processCommand(command: Command) {
        when (command) {
            is Command.Idle -> Unit
            is Command.Restart -> {
                if (v2rayInteractor.currentState != ConnectionState.Disconnected) {
                    val profile = profileInteractor.getSelectedProfile()
                    val ruleset = routingInteractor.getSelectedRuleset()
                    val filterConfig = userApplicationInteractor.getFilterConfig()

                    if (profile != null) {
                        v2rayInteractor.restartV2Ray(V2RayConfigBuildUseCase(profile, ruleset), filterConfig)
                    }
                }
            }
            is Command.Start -> {
                val profile = profileInteractor.getSelectedProfile()
                val ruleset = routingInteractor.getSelectedRuleset()
                val filterConfig = userApplicationInteractor.getFilterConfig()

                if (profile != null) {
                    v2rayInteractor.startV2ray(V2RayConfigBuildUseCase(profile, ruleset), filterConfig)
                }
            }
            is Command.Stop -> {
                v2rayInteractor.stopV2ray()
            }
        }
    }

    fun sendStartCommand() {
        commandBufferFlow.value = Command.Start()
    }

    private fun sendRestartCommand() {
        commandBufferFlow.value = Command.Restart()
    }

    fun sendStopCommand() {
        commandBufferFlow.value = Command.Stop()
    }

    suspend fun applyConfiguration() {
        val profile = profileInteractor.getSelectedProfile()
        val ruleset = routingInteractor.getSelectedRuleset()
        val filterConfig = userApplicationInteractor.getFilterConfig()

        if (profile != null) {
            v2rayInteractor.applyConfig(V2RayConfigBuildUseCase(profile, ruleset), filterConfig)

            if (v2rayInteractor.currentState == ConnectionState.Connected) {
                sendRestartCommand()
            }
        }
    }
}
