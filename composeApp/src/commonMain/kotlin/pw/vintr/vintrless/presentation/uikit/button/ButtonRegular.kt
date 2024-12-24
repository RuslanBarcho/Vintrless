package pw.vintr.vintrless.presentation.uikit.button

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pw.vintr.vintrless.presentation.theme.AppColor
import pw.vintr.vintrless.presentation.theme.RubikMedium16
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme

enum class ButtonRegularSize {
    DEFAULT,
    MEDIUM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonRegular(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    wrapContentWidth: Boolean = false,
    size: ButtonRegularSize = ButtonRegularSize.DEFAULT,
    color: Color = VintrlessExtendedTheme.colors.regularButtonBackground,
    contentColor: Color = VintrlessExtendedTheme.colors.regularButtonContent,
    disabledColor: Color = VintrlessExtendedTheme.colors.regularButtonDisabledBackground,
    disabledContentColor: Color = VintrlessExtendedTheme.colors.regularButtonDisabledContent,
    onClick: () -> Unit,
) {
    val height = remember(size) {
        when (size) {
            ButtonRegularSize.DEFAULT -> 48.dp
            ButtonRegularSize.MEDIUM -> 36.dp
        }
    }
    val ripple = RippleConfiguration(
        color = AppColor.White,
    )

    CompositionLocalProvider(LocalRippleConfiguration provides ripple) {
        Button(
            onClick = onClick,
            modifier = modifier
                .let {
                    if (wrapContentWidth) {
                        it.wrapContentWidth()
                    } else {
                        it.fillMaxWidth()
                    }
                }
                .height(height)
                .defaultMinSize(minHeight = height),
            shape = RoundedCornerShape(10.dp),
            enabled = enabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = contentColor,
                disabledContainerColor = if (isLoading) color else disabledColor,
                disabledContentColor = disabledContentColor,
            ),
        ) {
            if (!isLoading) {
                Text(
                    text = text,
                    style = RubikMedium16(),
                    color = if (enabled) { contentColor } else { disabledContentColor },
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = contentColor,
                )
            }
        }
    }
}
