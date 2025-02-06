package pw.vintr.vintrless.tools.painter

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun suspendBitmapPainter(
    placeholder: Painter = EmptyPainter,
    bitmapLoader: suspend () -> ImageBitmap?,
): Painter {
    var state by remember { mutableStateOf(placeholder ) }

    LaunchedEffect(Unit) {
        if (state == placeholder) {
            bitmapLoader()?.let { state = BitmapPainter(it) }
        }
    }

    return state
}

object EmptyPainter : Painter() {
    override val intrinsicSize: Size get() = Size.Unspecified
    override fun DrawScope.onDraw() {}
}
