package pw.vintr.vintrless.tools.extensions

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.cardShadow

@Composable
fun Modifier.cardBackground(cornerRadius: Dp = 20.dp) = this
    .cardBackground(RoundedCornerShape(cornerRadius))

@Composable
fun Modifier.cardBackground(shape: Shape) = this
    .clip(shape)
    .background(VintrlessExtendedTheme.colors.cardBackgroundColor)
    .border(
        width = 1.dp,
        color = VintrlessExtendedTheme.colors.cardStrokeColor,
        shape = shape
    )

@Composable
fun Modifier.selectableCardBackground(
    selected: Boolean,
    shape: Shape = RoundedCornerShape(12.dp),
): Modifier {
    val borderColor = animateColorAsState(
        targetValue = if (selected) {
            VintrlessExtendedTheme.colors.navBarSelected
        } else {
            VintrlessExtendedTheme.colors.cardStrokeColor
        }
    )
    val shadowColor = animateColorAsState(
        targetValue = if (selected) {
            VintrlessExtendedTheme.colors.navBarSelected
        } else {
            VintrlessExtendedTheme.colors.shadow
        }
    )

    return cardShadow(
        cornersRadius = 12.dp,
        color = shadowColor.value.copy(alpha = 0.25f)
    )
        .clip(shape)
        .background(VintrlessExtendedTheme.colors.cardBackgroundColor)
        .border(BorderStroke(1.dp, borderColor.value), shape)
}

@Composable
fun Modifier.fillMaxWidthRestricted(scope: BoxWithConstraintsScope, maxWidth: Dp, decreaseSize: Dp = 0.dp) = this
    .width(
        with(LocalDensity.current) { scope.constraints.maxWidth.toDp() }
            .coerceAtMost(maxWidth) - decreaseSize
    )

@Composable
fun Modifier.fillMaxHeightRestricted(scope: BoxWithConstraintsScope, maxHeight: Dp, decreaseSize: Dp = 0.dp) = this
    .height(
        with(LocalDensity.current) { scope.constraints.maxHeight.toDp() }
            .coerceAtMost(maxHeight) - decreaseSize
    )

fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }

    if (isFocused) {
        val imeIsVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
        val focusManager = LocalFocusManager.current

        LaunchedEffect(imeIsVisible) {
            if (imeIsVisible) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }
    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}
