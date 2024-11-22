package pw.vintr.vintrless

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.NativePaint
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter
import platform.UIKit.UIDevice
import pw.vintr.vintrless.presentation.navigation.BottomSheetNavigator

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

class IOSBottomSheetNavigator(sheetState: ModalBottomSheetState) : BottomSheetNavigator(sheetState)

actual fun getBottomSheetNavigator(
    sheetState: ModalBottomSheetState
): BottomSheetNavigator = IOSBottomSheetNavigator(sheetState)

actual fun getPlatform(): Platform = IOSPlatform()

@Composable
actual fun applyThemeOnView(darkTheme: Boolean) {}

actual fun NativePaint.setMaskFilter(blurRadius: Float) {
    this.maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, blurRadius / 2, true)
}

@OptIn(ExperimentalSettingsApi::class)
actual fun FlowSettings() = NSUserDefaultsSettings.Factory().create().toFlowSettings()
