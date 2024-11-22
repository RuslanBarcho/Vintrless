package pw.vintr.vintrless.domain.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import pw.vintr.vintrless.tools.closeable.Closeable

abstract class BaseInteractor : CoroutineScope, Closeable {

    private val job = SupervisorJob()

    override val coroutineContext = Dispatchers.Main + job

    override fun close() {
        if (isActive) cancel()
    }
}
