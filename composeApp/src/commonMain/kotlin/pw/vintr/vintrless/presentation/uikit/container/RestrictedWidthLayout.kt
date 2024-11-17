package pw.vintr.vintrless.presentation.uikit.container

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pw.vintr.vintrless.tools.extensions.fillMaxWidthRestricted

@Composable
fun RestrictedWidthLayout(
    restrictionWidth: Dp = 650.dp,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidthRestricted(maxWidth = restrictionWidth)
                .align(Alignment.Center)
        ) {
            content()
        }
    }
}
