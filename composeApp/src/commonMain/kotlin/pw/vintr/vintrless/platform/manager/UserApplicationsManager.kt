package pw.vintr.vintrless.platform.manager

import androidx.compose.ui.graphics.ImageBitmap
import pw.vintr.vintrless.domain.userApplications.model.UserApplication

expect object UserApplicationsManager {

    suspend fun getUserApplications(): List<UserApplication>

    suspend fun getApplicationIcon(application: UserApplication): ImageBitmap?
}
