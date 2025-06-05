package pw.vintr.vintrless.domain.system.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellableContinuation

class SudoPasswordState {
    var password by mutableStateOf<String?>(value = null)
    var isWindowOpen by mutableStateOf(value = false)
    var requestReason by mutableStateOf(value = SudoPasswordRequestReason.START_TUN)
    var continuation: List<CancellableContinuation<String?>> by mutableStateOf(value = listOf())
}
