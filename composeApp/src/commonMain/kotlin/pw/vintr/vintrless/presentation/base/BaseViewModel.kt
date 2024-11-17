package pw.vintr.vintrless.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.navigation.navResult.ResultListenerHandler

abstract class BaseViewModel(
    protected val navigator: AppNavigator
) : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext = Dispatchers.Main + job

    // private val primaryLoaderInteractor: PrimaryLoaderInteractor by inject()

    private val resultHandlerListeners: MutableMap<String, ResultListenerHandler> = mutableMapOf()

    open fun navigateBack(type: NavigatorType? = null) { navigator.back(type) }

    protected fun createExceptionHandler(
        onException: (Throwable) -> Unit = { }
    ) = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        onException.invoke(throwable)
    }

    protected fun <T> MutableStateFlow<BaseScreenState<T>>.loadWithStateHandling(
        emptyCheckAction: (T) -> Boolean = { _ -> false },
        block: suspend () -> T,
    ) = launch(createExceptionHandler { value = BaseScreenState.Error() }) {
        value = BaseScreenState.Loading()
        val data = block()

        value = if (emptyCheckAction(data)) {
            BaseScreenState.Empty()
        } else {
            BaseScreenState.Loaded(data)
        }
    }

    protected fun <T> MutableStateFlow<BaseScreenState<T>>.refreshWithStateHandling(
        block: suspend () -> T
    ) = launch(createExceptionHandler { value = BaseScreenState.Error() }) {
        val lockedValue = value

        if (lockedValue is BaseScreenState.Loaded) {
            value = BaseScreenState.Loaded(lockedValue.data, isRefreshing = true)
            value = BaseScreenState.Loaded(block())
        }
    }

    suspend fun withLoading(
        setLoadingCallback: (Boolean) -> Unit,
        showLoading: Boolean = true,
        action: suspend () -> Unit,
    ) {
        try {
            if (showLoading) {
                setLoadingCallback(true)
            }
            action()
            if (showLoading) {
                setLoadingCallback(false)
            }
        } catch (e: Throwable) {
            setLoadingCallback(false)
            throw e
        }
    }

    suspend fun withPrimaryLoader(action: suspend () -> Unit) {
        withLoading(
            setLoadingCallback = { isLoading ->
                // primaryLoaderInteractor.setLoaderState(isLoading)
            },
            action = action
        )
    }

    protected fun <T> Flow<T>.stateInThis(initialValue: T) = stateIn(
        scope = this@BaseViewModel,
        started = SharingStarted.Lazily,
        initialValue = initialValue,
    )

    protected fun handleResult(key: String, block: () -> ResultListenerHandler) {
        resultHandlerListeners += key to block()
    }

    override fun onCleared() {
        // Cancel all suspend operations
        if (isActive) cancel()

        // Clear all result handlers
        resultHandlerListeners.forEach { it.value.dispose() }
        resultHandlerListeners.clear()

        super.onCleared()
    }
}
