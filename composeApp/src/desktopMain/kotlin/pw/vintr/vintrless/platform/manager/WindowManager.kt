package pw.vintr.vintrless.platform.manager

import pw.vintr.vintrless.platform.model.WindowCornerRadius

actual object WindowManager {

    actual fun getWindowCornerRadius(): WindowCornerRadius {
        return WindowCornerRadius()
    }
}
