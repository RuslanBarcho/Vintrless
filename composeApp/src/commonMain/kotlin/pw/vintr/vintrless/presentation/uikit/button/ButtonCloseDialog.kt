package pw.vintr.vintrless.presentation.uikit.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_close

@Composable
fun ColumnScope.ButtonCloseDialog(backHandler: () -> Unit) {
    Icon(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .align(Alignment.End)
            .size(24.dp)
            .clip(CircleShape)
            .clickable { backHandler() },
        painter = painterResource(Res.drawable.ic_close),
        contentDescription = null,
    )
}
