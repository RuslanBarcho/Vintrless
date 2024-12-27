package pw.vintr.vintrless.presentation.uikit.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pw.vintr.vintrless.presentation.theme.RubikMedium16
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_arrow_right

@Composable
fun MenuActionWithIcon(
    modifier: Modifier = Modifier,
    iconRes: DrawableResource,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 32.dp)
    ) {
        Image(
            modifier = Modifier
                .size(20.dp),
            painter = painterResource(iconRes),
            contentDescription = null,
            colorFilter = ColorFilter.tint(VintrlessExtendedTheme.colors.textRegular)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = RubikMedium16(),
            color = VintrlessExtendedTheme.colors.textRegular,
        )
    }
}

@Composable
fun MenuAction(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 32.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = RubikMedium16(),
            color = VintrlessExtendedTheme.colors.textRegular,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            modifier = Modifier
                .size(20.dp),
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
        )
    }
}
