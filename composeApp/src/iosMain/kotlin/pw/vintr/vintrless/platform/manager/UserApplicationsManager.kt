package pw.vintr.vintrless.platform.manager

import androidx.compose.ui.graphics.ImageBitmap
import pw.vintr.vintrless.domain.userApplications.model.UserApplication

actual object UserApplicationsManager {

    actual suspend fun getUserApplications(): List<UserApplication> {
        return listOf()
    }

    actual suspend fun getApplicationIcon(application: UserApplication): ImageBitmap? {
        return null
    }
}
