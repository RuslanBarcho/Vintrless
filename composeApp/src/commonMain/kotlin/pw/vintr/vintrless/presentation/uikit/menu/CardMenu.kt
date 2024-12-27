package pw.vintr.vintrless.presentation.uikit.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pw.vintr.vintrless.presentation.theme.Gilroy12
import pw.vintr.vintrless.presentation.theme.Gilroy16
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.cardShadow
import pw.vintr.vintrless.presentation.uikit.separator.LineSeparator
import pw.vintr.vintrless.tools.extensions.cardBackground
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_arrow_right

data class CardMenuItemData(
    val iconRes: DrawableResource? = null,
    val title: String,
    val description: String,
    val onClick: () -> Unit,
)

@Composable
fun CardMenu(
    modifier: Modifier = Modifier,
    itemsData: List<CardMenuItemData>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardShadow(cornersRadius = 20.dp)
            .cardBackground(cornerRadius = 20.dp)
            .padding(vertical = 12.dp)
    ) {
        itemsData.forEachIndexed { index, itemData ->
            CardMenuItem(itemData)
            if (index != itemsData.lastIndex) {
                LineSeparator(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                )
            }
        }
    }
}

@Composable
private fun CardMenuItem(
    itemData: CardMenuItemData
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { itemData.onClick() }
            .padding(horizontal = 32.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemData.iconRes?.let { iconRes ->
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = VintrlessExtendedTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.width(20.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = itemData.title,
                style = Gilroy16(),
                color = VintrlessExtendedTheme.colors.textRegular,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = itemData.description,
                style = Gilroy12(),
                color = VintrlessExtendedTheme.colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = null,
        )
    }
}
