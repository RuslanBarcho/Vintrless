package pw.vintr.vintrless.presentation.uikit.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pw.vintr.vintrless.presentation.theme.*
import pw.vintr.vintrless.presentation.uikit.button.ButtonSimpleIcon
import pw.vintr.vintrless.tools.extensions.Empty
import pw.vintr.vintrless.tools.extensions.cardBackground
import pw.vintr.vintrless.tools.extensions.clearFocusOnKeyboardDismiss
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_clear

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    value: String = String.Empty,
    onValueChange: (String) -> Unit = {},
    hint: String = String.Empty,
    label: String = String.Empty,
    leadingIconRes: DrawableResource? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    showClearButton: Boolean = false,
    actionOnClear: (() -> Unit) = {},
    actionOnDone: ((String) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val localFocusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = label,
                style = Gilroy12(),
                color = LocalVintrColors.current.textLabel
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        BasicTextField(
            value = value,
            modifier = textFieldModifier
                .fillMaxWidth()
                .textFieldShadow(cornersRadius = 8.dp)
                .cardBackground(cornerRadius = 8.dp)
                .defaultMinSize(
                    minWidth = 64.dp,
                    minHeight = 40.dp
                )
                .clearFocusOnKeyboardDismiss(),
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = RubikRegular16().copy(
                color = VintrlessExtendedTheme.colors.textFieldContent,
            ),
            cursorBrush = SolidColor(VintrlessExtendedTheme.colors.textFieldContent),
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(
                onDone = {
                    localFocusManager.clearFocus()
                    actionOnDone?.invoke(value)
                },
                onSearch = {
                    localFocusManager.clearFocus()
                    actionOnDone?.invoke(value)
                }
            ),
            interactionSource = interactionSource,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            decorationBox = @Composable { innerTextField ->
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Leading icon
                    leadingIconRes?.let { res ->
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(res),
                            tint = VintrlessExtendedTheme.colors.textFieldHint,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    // Text field
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = hint,
                                style = RubikRegular16(),
                                color = VintrlessExtendedTheme.colors.textFieldHint,
                            )
                        }

                        innerTextField()
                    }

                    // Clear button
                    if (showClearButton) {
                        Spacer(modifier = Modifier.width(10.dp))
                        ButtonSimpleIcon(
                            iconRes = Res.drawable.ic_clear,
                            tint = VintrlessExtendedTheme.colors.textFieldHint,
                            onClick = actionOnClear
                        )
                    }
                }
            }
        )
    }
}
