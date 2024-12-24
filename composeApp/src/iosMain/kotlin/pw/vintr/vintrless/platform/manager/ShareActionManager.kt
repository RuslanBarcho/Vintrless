package pw.vintr.vintrless.platform.manager

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual object ShareActionManager {

    actual val canOpenActionSheet = true

    actual fun shareText(text: String) {
        val currentViewController = UIApplication.sharedApplication().keyWindow?.rootViewController
        val activityViewController = UIActivityViewController(listOf(text), null)

        currentViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }
}