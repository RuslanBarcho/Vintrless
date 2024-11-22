package pw.vintr.vintrless.presentation.uikit.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme

@Composable
fun <T> ScreenStateLayout(
    modifier: Modifier = Modifier,
    state: BaseScreenState<T>,
    errorRetryAction: () -> Unit = {},
    loading: @Composable () -> Unit = {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = VintrlessExtendedTheme.colors.navBarSelected,
            )
        }
    },
    error: @Composable () -> Unit = {},
    empty: @Composable () -> Unit = {},
    other: @Composable (BaseScreenState<T>) -> Unit = {},
    loaded: @Composable (BaseScreenState.Loaded<T>) -> Unit,
) {
    Box(modifier = modifier) {
        when (state) {
            is BaseScreenState.Loading -> {
                loading()
            }
            is BaseScreenState.Error -> {
                error()
            }
            is BaseScreenState.Loaded -> {
                loaded(state)
            }
            is BaseScreenState.Empty -> {
                empty()
            }
            else -> {
                other(state)
            }
        }
    }
}
