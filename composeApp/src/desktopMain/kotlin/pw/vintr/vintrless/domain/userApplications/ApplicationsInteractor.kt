package pw.vintr.vintrless.domain.userApplications

import androidx.compose.ui.graphics.ImageBitmap
import pw.vintr.vintrless.domain.system.interactor.SystemInteractor
import pw.vintr.vintrless.domain.system.model.OS
import pw.vintr.vintrless.domain.userApplications.model.common.process.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.common.application.UserApplication

abstract class ApplicationsInteractor {

    companion object {
        fun getInstance(): ApplicationsInteractor {
            return when (SystemInteractor.getOSType()) {
                OS.Windows -> WindowsApplicationsInteractor()
                OS.MacOS -> MacOSApplicationsInteractor()
                // TODO: Linux implementation
                OS.Linux -> WindowsApplicationsInteractor()
            }
        }
    }

    abstract suspend fun getApplications(): List<UserApplication>

    abstract suspend fun getRunningProcesses(): List<SystemProcess>

    abstract suspend fun getApplicationIcon(application: UserApplication): ImageBitmap?
}
