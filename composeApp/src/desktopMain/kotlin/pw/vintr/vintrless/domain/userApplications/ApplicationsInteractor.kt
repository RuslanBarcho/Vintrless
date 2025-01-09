package pw.vintr.vintrless.domain.userApplications

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skiko.toBitmap
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
import pw.vintr.vintrless.domain.system.interactor.SystemInteractor
import pw.vintr.vintrless.domain.system.model.OS
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.filechooser.FileSystemView

abstract class ApplicationsInteractor {

    companion object {
        fun getInstance(): ApplicationsInteractor {
            return when (SystemInteractor.getOSType()) {
                OS.Windows -> WindowsApplicationsInteractor()
                // TODO: other OS interactor
                OS.MacOS,
                OS.Linux -> WindowsApplicationsInteractor()
            }
        }
    }

    abstract suspend fun getApplications(): List<UserApplication>

    suspend fun getApplicationIcon(application: UserApplication): ImageBitmap? {
        return application.executablePath?.let { path ->
            getExecutableBufferedImage(File(path))
                ?.toBitmap()
                ?.asComposeImageBitmap()
        }
    }

    private suspend fun getExecutableBufferedImage(executable: File): BufferedImage? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val icon = FileSystemView
                    .getFileSystemView()
                    .getSystemIcon(executable, 500, 500)

                if (icon is BufferedImage) {
                    return@runCatching icon
                }

                // Create a buffered image with transparency
                val bufferedImage = BufferedImage(
                    icon.iconWidth,
                    icon.iconHeight,
                    BufferedImage.TYPE_INT_ARGB
                )

                // Draw the image on to the buffered image
                val bGr = bufferedImage.createGraphics()
                icon.paintIcon(null, bGr, 0, 0)
                bGr.dispose()

                // Return the buffered image
                bufferedImage
            }.getOrNull()
        }
    }
}
