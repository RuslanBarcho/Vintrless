package pw.vintr.vintrless.presentation.navigation.navResult

fun interface ResultListener {
    fun onResult(data: Any)
}

/**
 * Handler for manual delete subscription and avoid leak
 */
fun interface ResultListenerHandler {
    fun dispose()
}

class ResultWire {
    private val listeners = mutableMapOf<String, ResultListener>()

    fun setResultListener(key: String, listener: ResultListener): ResultListenerHandler {
        listeners[key] = listener
        return ResultListenerHandler {
            listeners.remove(key)
        }
    }

    fun sendResult(key: String, data: Any) {
        listeners.remove(key)?.onResult(data)
    }

    fun removeListener(key: String) {
        listeners.remove(key)
    }
}
