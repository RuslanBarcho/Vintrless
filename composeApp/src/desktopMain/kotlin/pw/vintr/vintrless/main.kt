package pw.vintr.vintrless

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import pw.vintr.vintrless.domain.system.interactor.SystemInteractor
import pw.vintr.vintrless.v2ray.interactor.JvmV2RayInteractor
import pw.vintr.vintrless.presentation.PasswordWindow
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_app_icon
import java.awt.Dimension

fun main() = application {
    PasswordWindow(SystemInteractor.sudoPasswordState)

    Window(
        onCloseRequest = {
            JvmV2RayInteractor.stopV2ray()
            exitApplication()
        },
        title = "Vintrless",
        icon = painterResource(Res.drawable.ic_app_icon),
        state = rememberWindowState(
            size = DpSize(
                width = 1000.dp,
                height = 700.dp
            )
        ),
    ) {
        window.minimumSize = Dimension(600, 600)
        App()
    }
}
