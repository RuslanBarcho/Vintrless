package pw.vintr.vintrless.tools.extensions

fun Process.close() {
    descendants()?.forEach(ProcessHandle::destroy)
    destroy()
    inputStream?.close()
    outputStream?.close()
}

fun Process.addShutdownHook() {
    val shutdownRunnable = Runnable { close() }
    Runtime.getRuntime().addShutdownHook(Thread(shutdownRunnable))
}
