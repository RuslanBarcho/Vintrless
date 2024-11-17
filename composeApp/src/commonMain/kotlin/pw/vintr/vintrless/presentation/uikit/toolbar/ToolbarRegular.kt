package pw.vintr.vintrless.presentation.uikit.toolbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pw.vintr.vintrless.presentation.theme.Gilroy18
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.tools.extensions.Empty
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_arrow_left

val REGULAR_TOOLBAR_HEIGHT = 56.dp

@Composable
fun ToolbarRegular(
    modifier: Modifier = Modifier,
    title: String = String.Empty,
    titleOpacity: Float = 1f,
    showBackButton: Boolean = true,
    onBackPressed: () -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(horizontal = 8.dp),
    backButtonColor: Color = VintrlessExtendedTheme.colors.textRegular,
    center: @Composable (BoxScope.() -> Unit)? = null,
    trailing: @Composable BoxScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(REGULAR_TOOLBAR_HEIGHT)
            .padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBackButton) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .clickable { onBackPressed() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = "Back button icon",
                    tint = backButtonColor
                )
            }
        } else {
            Box(modifier = Modifier.size(56.dp))
        }
        if (center != null) {
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                center()
            }
        } else {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .alpha(titleOpacity),
                text = title,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = Gilroy18(),
                color = VintrlessExtendedTheme.colors.textRegular,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .size(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            trailing()
        }
    }
}
