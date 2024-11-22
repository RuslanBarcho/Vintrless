package pw.vintr.vintrless.presentation.base

interface BaseScreenState<T> {

    val isLoaded: Boolean
        get() = this is Loaded

    class Loading<T> : BaseScreenState<T>

    class Error<T> : BaseScreenState<T>

    class Empty<T> : BaseScreenState<T>

    data class Loaded<T>(
        val payload: T,
        val isRefreshing: Boolean = false,
    ) : BaseScreenState<T>
}
