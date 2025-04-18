package pw.vintr.vintrless.presentation.screen.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.resolveOrientation
import pw.vintr.vintrless.domain.v2ray.model.ConnectionState
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.platform.manager.WindowManager
import pw.vintr.vintrless.platform.model.DeviceOrientation
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.theme.*
import pw.vintr.vintrless.presentation.uikit.button.SwitchButton
import pw.vintr.vintrless.presentation.uikit.layout.ScreenStateLayout
import pw.vintr.vintrless.presentation.uikit.navbar.NAV_BAR_HEIGHT_DP
import pw.vintr.vintrless.presentation.uikit.navbar.NAV_BAR_PADDING_DP
import pw.vintr.vintrless.tools.extensions.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.current_config
import vintrless.composeapp.generated.resources.no_config
import vintrless.composeapp.generated.resources.no_config_description
import vintrless.composeapp.generated.resources.state_disconnected
import vintrless.composeapp.generated.resources.state_connecting
import vintrless.composeapp.generated.resources.state_connected
import vintrless.composeapp.generated.resources.ic_arrow_right
import vintrless.composeapp.generated.resources.map_red
import vintrless.composeapp.generated.resources.map_blue
import vintrless.composeapp.generated.resources.ic_disconnected
import vintrless.composeapp.generated.resources.ic_connection
import vintrless.composeapp.generated.resources.ic_log

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val screenState = viewModel.screenState.collectAsState()
    val orientation = resolveOrientation()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(VintrlessExtendedTheme.colors.screenBackgroundColor),
        contentAlignment = Alignment.TopCenter,
    ) {
        ScreenStateLayout(
            modifier = Modifier
                .fillMaxSize(),
            state = screenState.value,
        ) { state ->
            when (orientation) {
                DeviceOrientation.PORTRAIT -> {
                    VerticalContent(
                        viewModel = viewModel,
                        state = state,
                    )
                }
                DeviceOrientation.LANDSCAPE -> {
                    HorizontalContent(
                        viewModel = viewModel,
                        state = state,
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.VerticalContent(
    viewModel: HomeViewModel,
    state: BaseScreenState.Loaded<HomeScreenState>
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            BackgroundMap(connectionState = state.payload.connectionState)

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.weight(1f))
                VPNSwitchButton(
                    connectionState = state.payload.connectionState,
                ) {
                    viewModel.toggle()
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.CenterHorizontally
                    ),
                ) {
                    ConnectionStateLabel(
                        connectionState = state.payload.connectionState
                    )
                    LogButton { viewModel.openLogs() }
                }
                Spacer(Modifier.height(64.dp))
            }
        }
        VerticalConfigCard(
            modifier = Modifier
                .fillMaxWidthRestricted(scope = this@VerticalContent, 640.dp),
            selectedProfile = state.payload.selectedProfile,
        ) {
            if (state.payload.selectedProfile != null) {
                viewModel.openProfileList()
            } else {
                viewModel.openCreateNewProfile()
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.HorizontalContent(
    viewModel: HomeViewModel,
    state: BaseScreenState.Loaded<HomeScreenState>
) {
    BackgroundMap(connectionState = state.payload.connectionState)
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = NAV_BAR_HEIGHT_DP.dp + NAV_BAR_PADDING_DP.dp + 16.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VPNSwitchButton(
                connectionState = state.payload.connectionState,
            ) {
                viewModel.toggle()
            }
            Spacer(Modifier.width(24.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                ConnectionStateLabel(
                    connectionState = state.payload.connectionState
                )
                LogButton { viewModel.openLogs() }
            }
        }
        Spacer(Modifier.width(32.dp))
        HorizontalConfigCard(
            selectedProfile = state.payload.selectedProfile,
        ) {
            if (state.payload.selectedProfile != null) {
                viewModel.openProfileList()
            } else {
                viewModel.openCreateNewProfile()
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.BackgroundMap(connectionState: ConnectionState) {
    Crossfade(targetState = connectionState) {
        when (it) {
            ConnectionState.Disconnected -> {
                Image(
                    modifier = Modifier
                        .fillMaxWidthRestricted(scope = this, 1170.dp)
                        .fillMaxHeightRestricted(scope = this, 578.dp),
                    painter = painterResource(Res.drawable.map_red),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }
            ConnectionState.Connecting,
            ConnectionState.Connected -> {
                Image(
                    modifier = Modifier
                        .fillMaxWidthRestricted(scope = this, 1170.dp)
                        .fillMaxHeightRestricted(scope = this, 578.dp),
                    painter = painterResource(Res.drawable.map_blue),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun VPNSwitchButton(
    connectionState: ConnectionState,
    onClick: () -> Unit,
) {
    val pulsating: Boolean
    val firstColor: Color
    val secondColor: Color

    when (connectionState) {
        ConnectionState.Disconnected -> {
            pulsating = false
            firstColor = AppColor.Siren
            secondColor = AppColor.Carmine
        }
        ConnectionState.Connecting -> {
            pulsating = true
            firstColor = AppColor.Cerulean
            secondColor = AppColor.Cyan
        }
        ConnectionState.Connected -> {
            pulsating = false
            firstColor = AppColor.Cerulean
            secondColor = AppColor.Cyan
        }
    }

    SwitchButton(
        pulsating = pulsating,
        firstColor = firstColor,
        secondColor = secondColor,
    ) {
        onClick()
    }
}

@Composable
fun ConnectionStateLabel(
    modifier: Modifier = Modifier,
    connectionState: ConnectionState,
) {
    Row(
        modifier = modifier
            .animateContentSize()
            .cardBackground(cornerRadius = 20.dp)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val opacity = remember { Animatable(initialValue = 1f) }

        LaunchedEffect(key1 = connectionState) {
            if (connectionState == ConnectionState.Connecting) {
                opacity.animateTo(
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(delayMillis = 200, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse,
                        initialStartOffset = StartOffset(offsetMillis = 0),
                    ),
                )
            } else {
                opacity.animateTo(targetValue = 1f)
            }
        }

        Crossfade(targetState = connectionState) {
            when (it) {
                ConnectionState.Disconnected -> {
                    Image(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(Res.drawable.ic_disconnected),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        alpha = opacity.value,
                    )
                }
                ConnectionState.Connecting,
                ConnectionState.Connected -> {
                    Image(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(Res.drawable.ic_connection),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        alpha = opacity.value,
                    )
                }
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = stringResource(when (connectionState) {
                ConnectionState.Disconnected -> {
                    Res.string.state_disconnected
                }
                ConnectionState.Connecting -> {
                    Res.string.state_connecting
                }
                ConnectionState.Connected -> {
                    Res.string.state_connected
                }
            }),
            style = Gilroy18(),
            color = VintrlessExtendedTheme.colors.textRegular,
        )
    }
}

@Composable
fun LogButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .cardBackground(cornerRadius = 20.dp)
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_log),
            tint = VintrlessExtendedTheme.colors.textRegular,
            contentDescription = null,
        )
    }
}

@Composable
private fun VerticalConfigCard(
    modifier: Modifier = Modifier,
    selectedProfile: ProfileData?,
    onClick: () -> Unit,
) {
    val windowCorners = WindowManager.getWindowCornerRadius()

    Box(
        modifier = modifier
            .animateContentSize()
            .cardBackground(
                RoundedCornerShape(
                    topStart = 50.dp,
                    topEnd = 50.dp,
                    bottomStart = with(LocalDensity.current) { windowCorners.bottomLeft.toDp() },
                    bottomEnd = with(LocalDensity.current) { windowCorners.bottomRight.toDp() },
                )
            )
            .navigationBarsPadding()
            .padding(
                bottom = NAV_BAR_HEIGHT_DP.dp + NAV_BAR_PADDING_DP.dp + 16.dp,
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(
                    top = 32.dp,
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 32.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(Res.string.current_config),
                    style = Gilroy24(),
                    color = VintrlessExtendedTheme.colors.textRegular,
                )
                Spacer(modifier = Modifier.height(20.dp))
                if (selectedProfile != null) {
                    SelectedProfileInfo(
                        profileData = selectedProfile
                    )
                } else {
                    NoProfileSelectedInfo()
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_right),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun HorizontalConfigCard(
    modifier: Modifier = Modifier,
    selectedProfile: ProfileData?,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .widthIn(max = 320.dp)
            .animateContentSize()
            .cardBackground(
                RoundedCornerShape(
                    topStart = 50.dp,
                    bottomStart = 50.dp,
                )
            )
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(
                    top = 32.dp,
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 32.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false),
            ) {
                Text(
                    text = stringResource(Res.string.current_config),
                    style = Gilroy24(),
                    color = VintrlessExtendedTheme.colors.textRegular,
                )
                Spacer(modifier = Modifier.height(20.dp))
                if (selectedProfile != null) {
                    SelectedProfileInfo(
                        profileData = selectedProfile
                    )
                } else {
                    NoProfileSelectedInfo()
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_right),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun NoProfileSelectedInfo(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(Res.string.no_config),
            style = Gilroy16(),
            color = VintrlessExtendedTheme.colors.textRegular,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(Res.string.no_config_description),
            style = Gilroy14(),
            color = VintrlessExtendedTheme.colors.textSecondary,
        )
    }
}

@Composable
private fun SelectedProfileInfo(
    modifier: Modifier = Modifier,
    profileData: ProfileData
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = profileData.name,
            color = VintrlessExtendedTheme.colors.textRegular,
            style = Gilroy16(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = profileData.ip +
                    String.Space +
                    String.Dot +
                    String.Space +
                    profileData.type.protocolName,
            color = VintrlessExtendedTheme.colors.textSecondary,
            style = RubikMedium16(),
        )
    }
}
