package pw.vintr.vintrless.presentation.uikit.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pw.vintr.vintrless.presentation.theme.Gilroy16
import pw.vintr.vintrless.presentation.theme.RubikMedium14
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.button.ButtonSimpleIcon
import pw.vintr.vintrless.tools.extensions.Empty
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_close

@Composable
fun Alert(
    modifier: Modifier = Modifier,
    title: String = String.Empty,
    message: String = String.Empty,
    backgroundColor: Color = VintrlessExtendedTheme.colors.positive,
    onCloseAction: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = title,
                style = Gilroy16(),
                color = VintrlessExtendedTheme.colors.textRegular,
            )
            ButtonSimpleIcon(
                iconRes = Res.drawable.ic_close,
                onClick = onCloseAction,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = message,
            style = RubikMedium14(),
            color = VintrlessExtendedTheme.colors.textRegular,
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}
