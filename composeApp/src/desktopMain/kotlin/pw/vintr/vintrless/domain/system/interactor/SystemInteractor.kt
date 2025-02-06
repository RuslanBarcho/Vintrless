package pw.vintr.vintrless.domain.system.interactor

import pw.vintr.vintrless.domain.system.model.OS

object SystemInteractor {

    private var detectedOS: OS? = null

    fun getOSType(): OS {
        return detectedOS ?: run {
            val osName = System.getProperty("os.name", "generic").lowercase()

            when {
                osName.contains("mac") || osName.contains("darwin") -> OS.MacOS
                osName.contains("win") -> OS.Windows
                else -> OS.Linux
            }.also {
                detectedOS = it
            }
        }
    }
}
