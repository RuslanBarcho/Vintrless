package pw.vintr.vintrless.platform.manager

import android.os.Build
import android.view.RoundedCorner
import pw.vintr.vintrless.platform.model.WindowCornerRadius
import pw.vintr.vintrless.tools.AppActivity

actual object WindowManager {

    actual fun getWindowCornerRadius(): WindowCornerRadius {
        val activity = AppActivity.get()
        val insets = activity.window.decorView.rootWindowInsets

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            WindowCornerRadius(
                topLeft = insets.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius ?: 0,
                topRight = insets.getRoundedCorner(RoundedCorner.POSITION_TOP_RIGHT)?.radius ?: 0,
                bottomLeft = insets.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_LEFT)?.radius ?: 0,
                bottomRight = insets.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_RIGHT)?.radius ?: 0,
            )
        } else {
            WindowCornerRadius()
        }
    }
}
