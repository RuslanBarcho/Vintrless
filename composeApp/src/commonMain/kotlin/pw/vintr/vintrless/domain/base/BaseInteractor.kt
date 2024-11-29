package pw.vintr.vintrless.domain.base

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import pw.vintr.vintrless.tools.closeable.Closeable

abstract class BaseInteractor : CoroutineScope, Closeable {

    private val job = SupervisorJob()

    override val coroutineContext = Dispatchers.Main + job

    private val _event = Channel<InteractorEvent>()

    val event by lazy { _event.receiveAsFlow() }

    protected suspend fun sendEvent(event: InteractorEvent) = _event.send(event)

    protected fun sendEventSync(event: InteractorEvent) = launch { _event.send(event) }

    override fun close() {
        if (isActive) cancel()
    }
}
