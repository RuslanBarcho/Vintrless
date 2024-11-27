package pw.vintr.vintrless.presentation.uikit.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme

@Composable
fun ButtonText(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    contentColor: Color = VintrlessExtendedTheme.colors.textButtonContent,
    disabledContentColor: Color = VintrlessExtendedTheme.colors.textButtonDisabledContent,
    onClick: () -> Unit,
) {
    ButtonRegular(
        modifier = modifier,
        text = text,
        enabled = enabled,
        isLoading = isLoading,
        color = Color.Transparent,
        contentColor = contentColor,
        disabledColor = Color.Transparent,
        disabledContentColor = disabledContentColor,
        onClick = onClick,
    )
}
