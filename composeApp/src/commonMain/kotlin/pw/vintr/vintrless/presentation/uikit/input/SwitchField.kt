package pw.vintr.vintrless.presentation.uikit.input

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pw.vintr.vintrless.presentation.theme.*
import pw.vintr.vintrless.tools.extensions.cardBackground

@Composable
fun SwitchField(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp)
            .cardShadow(cornersRadius = 8.dp)
            .cardBackground(cornerRadius = 8.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = RubikMedium16(),
            color = VintrlessExtendedTheme.colors.textRegular,
        )
        Spacer(Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = switchColors()
        )
    }
}
