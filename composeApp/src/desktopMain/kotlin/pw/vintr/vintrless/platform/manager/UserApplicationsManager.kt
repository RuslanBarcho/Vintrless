package pw.vintr.vintrless.platform.manager

import androidx.compose.ui.graphics.ImageBitmap
import pw.vintr.vintrless.domain.userApplications.ApplicationsInteractor
import pw.vintr.vintrless.domain.userApplications.model.UserApplication

actual object UserApplicationsManager {

    private val applicationsInteractor = ApplicationsInteractor.getInstance()

    actual suspend fun getUserApplications(): List<UserApplication> {
        return applicationsInteractor.getApplications()
    }

    actual fun getApplicationIcon(application: UserApplication): ImageBitmap? {
        return applicationsInteractor.getApplicationIcon(application)
    }
}
