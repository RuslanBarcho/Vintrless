package pw.vintr.vintrless.presentation.uikit.selector

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme

private const val RADIO_ANIMATION_DURATION = 100

private val radioButtonSize = 24.dp
private val radioButtonDotSize = 14.dp
private val radioStrokeWidth = 1.dp

@Composable
fun AppRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    defaultPadding: Dp = 0.dp,
    size: Dp = radioButtonSize,
) {
    val dotRadius = animateDpAsState(
        targetValue = if (selected) radioButtonDotSize / 2 else 0.dp,
        animationSpec = tween(durationMillis = RADIO_ANIMATION_DURATION),
        label = "DotRadius"
    )
    val radioColor = colors.radioColor(enabled, selected)
    val selectableModifier = if (onClick != null) {
        Modifier.selectable(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = interactionSource,
            indication = ripple(
                bounded = false,
                radius = (defaultPadding + size) / 2
            )
        )
    } else {
        Modifier
    }
    val radioRadius = size / 2

    val backgroundColor = VintrlessExtendedTheme.colors.radioBackground
    val strokeColor = VintrlessExtendedTheme.colors.radioStroke

    Canvas(
        modifier
            .then(selectableModifier)
            .wrapContentSize(Alignment.Center)
            .padding(defaultPadding)
            .requiredSize(size)
    ) {
        // Draw background
        drawCircle(
            backgroundColor,
            radius = radioRadius.toPx(),
            style = Fill
        )

        // Draw stroke
        val strokeWidth = radioStrokeWidth.toPx()
        drawCircle(
            strokeColor,
            radius = radioRadius.toPx() - strokeWidth / 2,
            style = Stroke(strokeWidth)
        )
        // Draw selection
        if (dotRadius.value > 0.dp) {
            drawCircle(
                radioColor.value,
                radius = dotRadius.value.toPx() - strokeWidth / 2,
                style = Fill,
            )
        }
    }
}

@Stable
interface RadioButtonColors {
    @Composable
    fun radioColor(enabled: Boolean, selected: Boolean): State<Color>
}

/**
 * Defaults used in [AppRadioButton].
 */
object RadioButtonDefaults {
    @Composable
    fun colors(
        selectedColor: Color = VintrlessExtendedTheme.colors.radioSelected,
        unselectedColor: Color = VintrlessExtendedTheme.colors.radioBackground,
        disabledColor: Color = VintrlessExtendedTheme.colors.radioBackground
            .copy(alpha = ContentAlpha.disabled)
    ): RadioButtonColors {
        return remember(
            selectedColor,
            unselectedColor,
            disabledColor
        ) {
            DefaultRadioButtonColors(selectedColor, unselectedColor, disabledColor)
        }
    }
}

/**
 * Default [RadioButtonColors] implementation.
 */
@Immutable
private class DefaultRadioButtonColors(
    private val selectedColor: Color,
    private val unselectedColor: Color,
    private val disabledColor: Color
) : RadioButtonColors {
    @Composable
    override fun radioColor(enabled: Boolean, selected: Boolean): State<Color> {
        val target = when {
            !enabled -> disabledColor
            !selected -> unselectedColor
            else -> selectedColor
        }

        // If not enabled 'snap' to the disabled state, as there should be no animations between
        // enabled / disabled.
        return if (enabled) {
            animateColorAsState(
                target,
                tween(durationMillis = RADIO_ANIMATION_DURATION),
                label = "ColorTransition",
            )
        } else {
            rememberUpdatedState(target)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DefaultRadioButtonColors

        if (selectedColor != other.selectedColor) return false
        if (unselectedColor != other.unselectedColor) return false
        if (disabledColor != other.disabledColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = selectedColor.hashCode()
        result = 31 * result + unselectedColor.hashCode()
        result = 31 * result + disabledColor.hashCode()
        return result
    }
}
