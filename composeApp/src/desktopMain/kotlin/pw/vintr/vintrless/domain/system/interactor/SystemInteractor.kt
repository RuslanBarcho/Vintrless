package pw.vintr.vintrless.domain.system.interactor

import kotlinx.coroutines.suspendCancellableCoroutine
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.system.model.OS
import pw.vintr.vintrless.domain.system.model.SudoPasswordRequestReason
import pw.vintr.vintrless.domain.system.model.SudoPasswordState

object SystemInteractor: BaseInteractor() {

    private var detectedOS: OS? = null

    val sudoPasswordState = SudoPasswordState()

    fun getOSType(): OS {
        return detectedOS ?: run {
            val osName = System.getProperty("os.name", "generic").lowercase()

            when {
                osName.contains("mac") || osName.contains("darwin") -> OS.MacOS
                osName.contains("win") -> OS.Windows
                else -> OS.Linux
            }.also {
                detectedOS = it
            }
        }
    }

    suspend fun getSudoPassword(requestReason: SudoPasswordRequestReason): String? {
        return if (sudoPasswordState.password == null) {
            sudoPasswordState.requestReason = requestReason

            suspendCancellableCoroutine { continuation ->
                sudoPasswordState.continuation += continuation
                sudoPasswordState.isWindowOpen = true
            }
        } else {
            sudoPasswordState.password
        }
    }
}
