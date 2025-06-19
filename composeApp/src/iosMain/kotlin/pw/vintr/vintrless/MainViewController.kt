package pw.vintr.vintrless

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import pw.vintr.vintrless.tools.modules.appModule

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}
