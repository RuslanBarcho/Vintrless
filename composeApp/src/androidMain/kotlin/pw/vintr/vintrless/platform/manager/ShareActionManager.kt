package pw.vintr.vintrless.platform.manager

import android.content.Intent
import pw.vintr.vintrless.tools.AppActivity

actual object ShareActionManager {

    actual val canOpenActionSheet = true

    actual fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val intentChooser = Intent.createChooser(intent, null)
        AppActivity.get().startActivity(intentChooser)
    }
}