package pw.vintr.vintrless.tools.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler

fun createExceptionHandler(
    onException: (Throwable) -> Unit = { }
) = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
    onException.invoke(throwable)
}
