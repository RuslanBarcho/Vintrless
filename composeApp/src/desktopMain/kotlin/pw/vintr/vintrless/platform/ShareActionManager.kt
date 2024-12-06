package pw.vintr.vintrless.platform

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual object ShareActionManager {

    actual val canOpenActionSheet = false

    actual fun shareText(text: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
    }
}
