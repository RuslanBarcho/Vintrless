package pw.vintr.vintrless.tools.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import pw.vintr.vintrless.presentation.base.BaseScreenState

inline fun <reified R> StateFlow<*>.withTyped(block: (R) -> Unit) {
    val lockedValue = value

    if (lockedValue is R) {
        block(lockedValue)
    }
}

inline fun <T> StateFlow<BaseScreenState<T>>.withLoaded(block: (T) -> Unit) {
    withTyped<BaseScreenState.Loaded<T>> { block(it.payload) }
}

inline fun <reified T, R> StateFlow<BaseScreenState<T>>.getWithLoaded(block: (T) -> R): R? {
    val lockedValue = value

    return if (lockedValue is T) {
        block(lockedValue)
    } else {
        null
    }
}

inline fun <reified R> MutableStateFlow<in R>.updateTyped(
    onDifferentType: () -> Unit = {},
    mutation: (R) -> R
) {
    val lockedValue = value

    if (lockedValue is R) {
        value = mutation(lockedValue)
    } else {
        onDifferentType()
    }
}

inline fun <T> MutableStateFlow<BaseScreenState<T>>.updateLoaded(
    onDifferentType: () -> Unit = {},
    mutation: (T) -> T,
) {
    updateTyped<BaseScreenState.Loaded<T>>(onDifferentType) { loaded ->
        loaded.copy(payload = mutation(loaded.payload))
    }
}

fun <T> Flow<T>.throttleLatest(delayMillis: Long): Flow<T> = this
    .conflate()
    .transform {
        emit(it)
        delay(delayMillis)
    }
