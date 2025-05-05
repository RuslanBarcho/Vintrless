package pw.vintr.vintrless.domain.system.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellableContinuation

class SudoPasswordState {
    var password by mutableStateOf<String?>(null)
    var isWindowOpen by mutableStateOf(false)
    var continuation: CancellableContinuation<String?>? by mutableStateOf(null)
}
