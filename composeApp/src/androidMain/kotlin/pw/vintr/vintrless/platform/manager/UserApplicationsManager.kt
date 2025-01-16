package pw.vintr.vintrless.platform.manager

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pw.vintr.vintrless.domain.userApplications.model.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
import pw.vintr.vintrless.domain.userApplications.model.UserApplicationPayload
import pw.vintr.vintrless.tools.AppContext

actual object UserApplicationsManager {

    actual suspend fun getUserApplications(): List<UserApplication> {
        val context = AppContext.get()
        val installedPackages = context.packageManager.getInstalledPackages(0)

        return withContext(Dispatchers.IO) {
            installedPackages.map { packageInfo ->
                val appInfo = context.packageManager.getApplicationInfo(packageInfo.packageName, 0)

                UserApplication(
                    name = context.packageManager.getApplicationLabel(appInfo).toString(),
                    payload = UserApplicationPayload.AndroidApplicationPayload(
                        packageName = packageInfo.packageName,
                    )
                )
            }.filter { it.name.isNotEmpty() }
        }
    }

    actual suspend fun getRunningProcesses(): List<SystemProcess> = listOf()

    actual suspend fun getApplicationIcon(application: UserApplication): ImageBitmap? {
        val context = AppContext.get()
        val payload = application.payload as? UserApplicationPayload.AndroidApplicationPayload ?: return null
        val iconDrawable = context.packageManager.getApplicationIcon(payload.packageName)

        return iconDrawable
            .toBitmap()
            .asImageBitmap()
    }
}
