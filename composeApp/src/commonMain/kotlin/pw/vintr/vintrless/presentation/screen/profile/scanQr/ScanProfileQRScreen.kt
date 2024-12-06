package pw.vintr.vintrless.presentation.screen.profile.scanQr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import qrscanner.CameraLens
import qrscanner.QrScanner
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.scanner_frame

@Composable
fun ScanProfileQrScreen(
    viewModel: ScanProfileQRViewModel = koinViewModel(),
) {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        topBar = {
            ToolbarRegular(
                onBackPressed = { viewModel.navigateBack() }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 36.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 1f)
            ) {
                QrScanner(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clipToBounds(),
                    flashlightOn = false,
                    cameraLens = CameraLens.Back,
                    openImagePicker = false,
                    imagePickerHandler = {},
                    onFailure = {},
                    onCompletion = { scannedText ->
                        viewModel.onTextScanned(scannedText)
                    },
                    customOverlay = {
                        drawContent()
                    }
                )
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(Res.drawable.scanner_frame),
                    contentDescription = null,
                )
            }
        }
    }
}
