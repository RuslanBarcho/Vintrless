package pw.vintr.vintrless.platform.manager

import androidx.compose.ui.graphics.ImageBitmap
import pw.vintr.vintrless.domain.userApplications.model.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.UserApplication

expect object UserApplicationsManager {

    suspend fun getUserApplications(): List<UserApplication>

    suspend fun getRunningProcesses(): List<SystemProcess>

    suspend fun getApplicationIcon(application: UserApplication): ImageBitmap?
}
