package pw.vintr.vintrless.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import pw.vintr.vintrless.domain.system.model.SudoPasswordState
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.VintrlessTheme
import pw.vintr.vintrless.presentation.uikit.button.ButtonRegular
import pw.vintr.vintrless.presentation.uikit.input.AppTextField
import kotlin.coroutines.resume

@Composable
fun PasswordWindow(
    state: SudoPasswordState,
    windowTitle: String = "Authentication Required"
) {
    val windowState = rememberWindowState(
        width = 350.dp,
        height = 400.dp,
    )

    if (state.isWindowOpen) {
        VintrlessTheme {
            Window(
                title = windowTitle,
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
                    Text("Reason")
                    Spacer(Modifier.height(8.dp))
                    AppTextField(
                        value = state.password.orEmpty(),
                        onValueChange = { state.password = it },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(16.dp))
                    ButtonRegular(
                        text = "Submit",
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
