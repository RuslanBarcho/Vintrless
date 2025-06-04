package pw.vintr.vintrless.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.stringResource
import pw.vintr.vintrless.domain.system.model.SudoPasswordState
import pw.vintr.vintrless.presentation.theme.Gilroy18
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.VintrlessTheme
import pw.vintr.vintrless.presentation.uikit.button.ButtonRegular
import pw.vintr.vintrless.presentation.uikit.button.ButtonSimpleIcon
import pw.vintr.vintrless.presentation.uikit.input.AppTextField
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.sudo_enter_password_auth_required
import vintrless.composeapp.generated.resources.sudo_enter_password_reason
import vintrless.composeapp.generated.resources.sudo_enter_password_submit
import vintrless.composeapp.generated.resources.ic_password_visible
import vintrless.composeapp.generated.resources.ic_password_hidden
import java.awt.Cursor
import kotlin.coroutines.resume

@Composable
fun PasswordWindow(
    state: SudoPasswordState,
) {
    val windowState = rememberWindowState(
        width = 350.dp,
        height = 400.dp,
        position = WindowPosition.Aligned(Alignment.Center)
    )
    var passwordVisible by remember { mutableStateOf(false) }

    if (state.isWindowOpen) {
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        VintrlessTheme {
            Window(
                title = stringResource(Res.string.sudo_enter_password_auth_required),
                onCloseRequest = {
                    state.isWindowOpen = false
                    state.continuation?.resume(null)
                    state.continuation = null
                },
                visible = state.isWindowOpen,
                resizable = false,
                state = windowState,
            ) {
                Column(
                    Modifier
                        .background(VintrlessExtendedTheme.colors.screenBackgroundColor)
                        .padding(28.dp)
                        .fillMaxSize()
                        .wrapContentHeight()
                ) {
                    Text(
                        text = stringResource(Res.string.sudo_enter_password_reason),
                        style = Gilroy18(),
                        color = VintrlessExtendedTheme.colors.textRegular,
                    )
                    Spacer(Modifier.height(24.dp))
                    AppTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        value = state.password.orEmpty(),
                        onValueChange = { state.password = it },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        singleLine = true,
                        trailingSlot = {
                            ButtonSimpleIcon(
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))),
                                iconRes = if (passwordVisible) {
                                    Res.drawable.ic_password_visible
                                } else {
                                    Res.drawable.ic_password_hidden
                                },
                                tint = VintrlessExtendedTheme.colors.textSecondary,
                                onClick = { passwordVisible = !passwordVisible }
                            )
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    ButtonRegular(
                        text = stringResource(Res.string.sudo_enter_password_submit),
                        onClick = {
                            state.isWindowOpen = false
                            state.continuation?.resume(state.password)
                            state.continuation = null
                        }
                    )
                }
            }
        }
    }
}
