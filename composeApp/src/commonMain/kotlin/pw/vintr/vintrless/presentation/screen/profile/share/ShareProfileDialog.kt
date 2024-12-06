package pw.vintr.vintrless.presentation.screen.profile.share

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pw.vintr.vintrless.presentation.navigation.NavigatorType
import pw.vintr.vintrless.presentation.theme.Gilroy12
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.layout.ScreenStateLayout
import pw.vintr.vintrless.presentation.uikit.menu.MenuActionWithIcon
import pw.vintr.vintrless.presentation.uikit.separator.LineSeparator
import pw.vintr.vintrless.tools.extensions.cardBackground
import qrgenerator.qrkitpainter.*
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_close

@Composable
fun ShareProfileDialog(
    dataId: String,
    viewModel: ShareProfileViewModel = koinViewModel { parametersOf(dataId) }
) {
    val screenState = viewModel.screenState.collectAsState()

    Column(
        modifier = Modifier
            .cardBackground(cornerRadius = 20.dp)
            .padding(vertical = 24.dp)
            .animateContentSize()
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .align(Alignment.End)
                .size(24.dp)
                .clip(CircleShape)
                .clickable { viewModel.navigateBack(NavigatorType.Root) },
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(10.dp))
        ScreenStateLayout(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = VintrlessExtendedTheme.colors.navBarSelected,
                    )
                }
            },
            state = screenState.value
        ) { state ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val textRegular = VintrlessExtendedTheme.colors.textRegular
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(200.dp),
                    painter = rememberQrKitPainter(
                        data = state.payload.profileUrl,
                        qrOptions = {
                            shapes = QrKitShapes(
                                ballShape = getSelectedQrBall(QrBallType.CircleQrBall()),
                                darkPixelShape = getSelectedPixel(QrPixelType.CirclePixel()),
                                frameShape = getSelectedFrameShape(QrFrameType.CircleFrame()),
                                codeShape = getSelectedPattern(PatternType.SquarePattern)
                            )
                            colors = QrKitColors(
                                darkBrush = QrKitBrush.solidBrush(textRegular)
                            )
                        }
                    ),
                    contentDescription = "Profile url QR"
                )
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    text = stringResource(Res.string.other_ways),
                    style = Gilroy12(),
                    color = VintrlessExtendedTheme.colors.textLabel,
                )
                Spacer(modifier = Modifier.height(10.dp))
                MenuActionWithIcon(
                    iconRes = Res.drawable.ic_url,
                    title = stringResource(Res.string.share_url)
                ) {
                    viewModel.performShareAction(state.payload.profileUrl)
                }
                LineSeparator(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                )
                MenuActionWithIcon(
                    iconRes = Res.drawable.ic_json,
                    title = stringResource(Res.string.share_json)
                ) {
                    viewModel.performShareAction(state.payload.profileJSON)
                }
            }
        }
    }
}
