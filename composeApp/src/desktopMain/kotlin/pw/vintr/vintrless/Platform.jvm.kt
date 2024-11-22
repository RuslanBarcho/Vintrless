package pw.vintr.vintrless

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.NativePaint
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter
import pw.vintr.vintrless.presentation.navigation.BottomSheetNavigator

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

class DesktopBottomSheetNavigator(sheetState: ModalBottomSheetState) : BottomSheetNavigator(sheetState)

actual fun getBottomSheetNavigator(
    sheetState: ModalBottomSheetState
): BottomSheetNavigator = DesktopBottomSheetNavigator(sheetState)

actual fun getPlatform(): Platform = JVMPlatform()

@Composable
actual fun applyThemeOnView(darkTheme: Boolean) {}

actual fun NativePaint.setMaskFilter(blurRadius: Float) {
    this.maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, blurRadius / 2, true)
}

@OptIn(ExperimentalSettingsApi::class)
actual fun FlowSettings() = PreferencesSettings.Factory().create().toFlowSettings()
