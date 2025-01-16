package pw.vintr.vintrless.platform.manager

import androidx.compose.ui.graphics.ImageBitmap
import pw.vintr.vintrless.domain.userApplications.ApplicationsInteractor
import pw.vintr.vintrless.domain.userApplications.model.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.UserApplication

actual object UserApplicationsManager {

    private val applicationsInteractor = ApplicationsInteractor.getInstance()

    actual suspend fun getUserApplications(): List<UserApplication> {
        return applicationsInteractor.getApplications()
    }

    actual suspend fun getRunningProcesses(): List<SystemProcess> {
        return applicationsInteractor.getRunningProcesses()
    }

    actual suspend fun getApplicationIcon(application: UserApplication): ImageBitmap? {
        return applicationsInteractor.getApplicationIcon(application)
    }
}
