package pw.vintr.vintrless

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_desktop_icon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Vintrless",
        icon = painterResource(Res.drawable.ic_desktop_icon)
    ) {
        App()
    }
}
