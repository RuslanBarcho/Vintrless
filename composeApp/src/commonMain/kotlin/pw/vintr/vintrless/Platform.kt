package pw.vintr.vintrless

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.NativePaint
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import pw.vintr.vintrless.domain.v2ray.interactor.V2rayPlatformInteractor
import pw.vintr.vintrless.presentation.navigation.BottomSheetNavigator

expect fun getBottomSheetNavigator(sheetState: ModalBottomSheetState): BottomSheetNavigator

@Composable
expect fun applyThemeOnView(darkTheme: Boolean)

expect fun NativePaint.setMaskFilter(blurRadius: Float)

@OptIn(ExperimentalSettingsApi::class)
expect fun FlowSettings(): FlowSettings

expect fun V2rayPlatformInteractor(): V2rayPlatformInteractor

expect fun cameraAvailable(): Boolean
