package pw.vintr.vintrless

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.NativePaint
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter
import org.lighthousegames.logging.logging
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayPlatformInteractor
import pw.vintr.vintrless.platform.PlatformType
import pw.vintr.vintrless.presentation.navigation.BottomSheetNavigator
import pw.vintr.vintrless.v2ray.interactor.JvmV2RayInteractor

class DesktopBottomSheetNavigator(sheetState: ModalBottomSheetState) : BottomSheetNavigator(sheetState)

actual fun getBottomSheetNavigator(
    sheetState: ModalBottomSheetState
): BottomSheetNavigator = DesktopBottomSheetNavigator(sheetState)

@Composable
actual fun applyThemeOnView(darkTheme: Boolean) {}

actual fun NativePaint.setMaskFilter(blurRadius: Float) {
    this.maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, blurRadius / 2, true)
}

@OptIn(ExperimentalSettingsApi::class)
actual fun FlowSettings() = PreferencesSettings.Factory().create().toFlowSettings()

actual fun V2RayPlatformInteractor(): V2RayPlatformInteractor = JvmV2RayInteractor

actual fun cameraAvailable() = false

actual fun platformType() = PlatformType.JVM
