package pw.vintr.vintrless.domain.userApplications

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import org.jetbrains.skiko.toBitmap
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
import pw.vintr.vintrless.domain.system.interactor.SystemInteractor
import pw.vintr.vintrless.domain.system.model.OS
import java.awt.image.BufferedImage
import java.io.File

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

    fun getApplicationIcon(application: UserApplication): ImageBitmap? {
        return application.executablePath?.let { path ->
            getExecutableBufferedImage(File(path))
                .toBitmap()
                .asComposeImageBitmap()
        }
    }

    private fun getExecutableBufferedImage(executable: File): BufferedImage {
        val sf = sun.awt.shell.ShellFolder.getShellFolder(executable)
        val icon = sf.getIcon(true)

        if (icon is BufferedImage) {
            return icon
        }

        // Create a buffered image with transparency
        val bufferedImage = BufferedImage(
            icon.getWidth(null),
            icon.getHeight(null),
            BufferedImage.TYPE_INT_ARGB
        )

        // Draw the image on to the buffered image
        val bGr = bufferedImage.createGraphics()
        bGr.drawImage(icon, 0, 0, null)
        bGr.dispose()

        // Return the buffered image
        return bufferedImage
    }
}
