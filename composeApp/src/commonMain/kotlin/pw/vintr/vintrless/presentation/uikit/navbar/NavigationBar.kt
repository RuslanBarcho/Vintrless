package pw.vintr.vintrless.presentation.uikit.navbar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.cardShadow

const val NAV_BAR_HEIGHT_DP = 64

const val NAV_BAR_PADDING_DP = 12

@Composable
fun AppNavigationBar(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit,
) {

    BoxWithConstraints(modifier = modifier) {
        Row(
            Modifier
                .windowInsetsPadding(windowInsets)
                .cardShadow(cornersRadius = 20.dp)
                .background(
                    color = VintrlessExtendedTheme.colors.cardBackgroundColor,
                    shape = RoundedCornerShape(20.dp),
                )
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = VintrlessExtendedTheme.colors.cardStrokeColor
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
                .widthIn(240.dp, with(LocalDensity.current) { constraints.maxWidth.toDp() })
                .height(NAV_BAR_HEIGHT_DP.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun AppNavBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    icon: DrawableResource,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    selectedContentColor: Color = VintrlessExtendedTheme.colors.navBarSelected,
    unselectedContentColor: Color = VintrlessExtendedTheme.colors.navBarUnselected,
) {
    val ripple = ripple(bounded = true, color = selectedContentColor)

    Box(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = ripple
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.size(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Icon
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = if (selected) selectedContentColor else unselectedContentColor,
            )
        }
    }
}
