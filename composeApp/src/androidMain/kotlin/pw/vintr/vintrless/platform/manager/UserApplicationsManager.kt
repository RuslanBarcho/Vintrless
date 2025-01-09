package pw.vintr.vintrless.platform.manager

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
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
                    processName = packageInfo.packageName,
                    executablePath = null,
                )
            }.filter { it.name.isNotEmpty() }
        }
    }

    actual fun getApplicationIcon(application: UserApplication): ImageBitmap? {
        val context = AppContext.get()
        val iconDrawable = context.packageManager.getApplicationIcon(application.processName)

        return iconDrawable
            .toBitmap()
            .asImageBitmap()
    }
}
