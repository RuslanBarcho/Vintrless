package pw.vintr.vintrless.presentation.uikit.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pw.vintr.vintrless.presentation.theme.Gilroy12
import pw.vintr.vintrless.presentation.theme.RubikMedium16
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme

enum class ButtonSecondarySize {
    DEFAULT,
    MEDIUM
}

@Composable
fun ButtonSecondary(
    modifier: Modifier = Modifier,
    text: String? = null,
    content: @Composable (RowScope.() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    wrapContentWidth: Boolean = false,
    size: ButtonSecondarySize = ButtonSecondarySize.DEFAULT,
    color: Color = VintrlessExtendedTheme.colors.secondaryButtonBackground,
    borderColor: Color = VintrlessExtendedTheme.colors.secondaryButtonStroke,
    contentColor: Color = VintrlessExtendedTheme.colors.secondaryButtonContent,
    disabledColor: Color = VintrlessExtendedTheme.colors.secondaryButtonDisabledBackground,
    disabledContentColor: Color = VintrlessExtendedTheme.colors.secondaryButtonDisabledContent,
    onClick: () -> Unit,
) {
    val height = remember(size) {
        when (size) {
            ButtonSecondarySize.DEFAULT -> 40.dp
            ButtonSecondarySize.MEDIUM -> 30.dp
        }
    }

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
        contentPadding = PaddingValues(
            vertical = 2.dp,
            horizontal = 10.dp
        ),
        border = if (enabled && !isLoading) {
            BorderStroke(1.dp, borderColor)
        } else {
            null
        }
    ) {
        if (!isLoading) {
            if (content != null) {
                content()
            } else if (text != null) {
                Text(
                    text = text,
                    style = when (size) {
                        ButtonSecondarySize.DEFAULT -> RubikMedium16()
                        ButtonSecondarySize.MEDIUM -> Gilroy12()
                    },
                    color = if (enabled) { contentColor } else { disabledContentColor },
                )
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = contentColor,
            )
        }
    }
}
