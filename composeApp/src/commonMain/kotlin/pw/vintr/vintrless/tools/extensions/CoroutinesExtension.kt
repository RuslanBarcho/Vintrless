package pw.vintr.vintrless.tools.extensions

import kotlinx.coroutines.Job

fun Job?.cancelIfActive() {
    if (this != null && isActive) {
        cancel()
    }
}
