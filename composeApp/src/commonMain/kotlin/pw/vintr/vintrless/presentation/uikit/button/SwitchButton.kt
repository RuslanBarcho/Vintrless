package pw.vintr.vintrless.presentation.uikit.button

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_energy

private const val BUTTON_CONTAINER_SIZE = 170

@Composable
fun SwitchButton(
    modifier: Modifier = Modifier,
    pulsating: Boolean = false,
    firstColor: Color,
    secondColor: Color,
    onClick: () -> Unit,
) {
    ButtonBox(modifier = modifier) {
        Crossfade(targetState = pulsating) { isPulsating ->
            if (isPulsating) {
                PulsatingRipple(color = secondColor)
            } else {
                ButtonBox {
                    PrimaryCircle(
                        color = secondColor,
                        size = 138.dp,
                        alpha = 0.65f,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .height(122.dp)
                .width(122.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            firstColor,
                            secondColor
                        )
                    )
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_energy),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ValueAnimationEffect(
    value: Animatable<Float, *>,
    targetValue: Float,
    initialOffset: StartOffset = StartOffset(0)
) {
    LaunchedEffect(key1 = true) {
        value.animateTo(
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = initialOffset,
            ),
        )
    }
}

@Composable
private fun PulsatingRipple(color: Color) {
    ButtonBox {
        val opacityFirstWave = remember { Animatable(0.5f) }
        val sizeFirstWave = remember { Animatable(122f) }

        val opacitySecondWave = remember { Animatable(0.5f) }
        val sizeSecondWave = remember { Animatable(122f) }

        ValueAnimationEffect(value = opacityFirstWave, targetValue = 0f)
        ValueAnimationEffect(value = sizeFirstWave, targetValue = 170f)

        ValueAnimationEffect(
            value = opacitySecondWave,
            targetValue = 0f,
            initialOffset = StartOffset(500)
        )
        ValueAnimationEffect(
            value = sizeSecondWave,
            targetValue = 170f,
            initialOffset = StartOffset(500)
        )

        PrimaryCircle(color = color, size = sizeFirstWave.value.dp, alpha = opacityFirstWave.value)
        PrimaryCircle(color = color, size = sizeSecondWave.value.dp, alpha = opacitySecondWave.value)
    }
}

@Composable
private fun PrimaryCircle(color: Color, size: Dp, alpha: Float) {
    Canvas(
        modifier = Modifier
            .size(size)
            .alpha(alpha)
    ) {
        drawCircle(color = color)
    }
}

@Composable
private fun ButtonBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.size(BUTTON_CONTAINER_SIZE.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}
