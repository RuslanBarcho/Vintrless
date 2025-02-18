package pw.vintr.vintrless.tools.compose

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pw.vintr.vintrless.platform.model.PlatformType
import pw.vintr.vintrless.platformType

enum class LazyListInteractionState {
    IDLE,
    INTERACTING
}

fun Modifier.lazyListInteraction(
    listState: LazyListState,
    block: (LazyListInteractionState) -> Unit
) = pointerInput(Unit) {
    awaitLazyListInteractionState(listState, block)
}

suspend fun PointerInputScope.awaitLazyListInteractionState(
    listState: LazyListState,
    block: (LazyListInteractionState) -> Unit
) {
    val currentContext = currentCoroutineContext()
    val platform = platformType()

    var currentState = LazyListInteractionState.IDLE
    var isUserScrolling = false

    fun emit(state: LazyListInteractionState) {
        if (currentState != state) {
            currentState = state
            block(state)
        }
    }

    when (platform) {
        PlatformType.ANDROID,
        PlatformType.IOS -> {
            with(CoroutineScope(currentContext)) {
                launch {
                    snapshotFlow { listState.isScrollInProgress }
                        .collectLatest { isScrolling ->
                            if (!isUserScrolling && !isScrolling) {
                                emit(LazyListInteractionState.IDLE)
                            }
                        }
                }
            }

            awaitEachGesture {
                awaitFirstDown()
                var event = awaitPointerEvent()
                while (event.type == PointerEventType.Move) {
                    isUserScrolling = true
                    emit(LazyListInteractionState.INTERACTING)
                    event = awaitPointerEvent()
                }

                isUserScrolling = false
            }
        }
        PlatformType.JVM -> {
            with(CoroutineScope(currentContext)) {
                launch {
                    snapshotFlow { listState.isScrollInProgress }
                        .collectLatest { isScrolling ->
                            if (!isScrolling) {
                                emit(LazyListInteractionState.IDLE)
                            }
                        }
                }
            }

            awaitPointerEventScope {
                while (currentContext.isActive) {
                    val event = awaitPointerEvent()
                    if (event.type == PointerEventType.Scroll && listState.isScrollInProgress) {
                        emit(LazyListInteractionState.INTERACTING)
                    }
                }
            }
        }
    }
}
