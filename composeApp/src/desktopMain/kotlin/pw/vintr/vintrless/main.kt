package pw.vintr.vintrless

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import pw.vintr.vintrless.v2ray.interactor.JvmV2RayInteractor
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_app_icon

fun main() = application {
    Window(
        onCloseRequest = {
            JvmV2RayInteractor.stopV2ray()
            exitApplication()
        },
        title = "Vintrless",
        icon = painterResource(Res.drawable.ic_app_icon)
    ) {
        App()
    }
}
