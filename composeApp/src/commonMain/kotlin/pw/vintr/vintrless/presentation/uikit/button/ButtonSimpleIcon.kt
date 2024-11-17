package pw.vintr.vintrless.presentation.uikit.button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme

@Composable
fun ButtonSimpleIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    iconRes: DrawableResource,
    onClick: () -> Unit,
    size: Dp = 24.dp,
    tint: Color = VintrlessExtendedTheme.colors.textRegular
) {
    IconButton(
        modifier = modifier.size(size),
        onClick = onClick
    ) {
        Icon(
            modifier = iconModifier,
            painter = painterResource(iconRes),
            contentDescription = "Simple icon button",
            tint = tint
        )
    }
}
