package pw.vintr.vintrless

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.NativePaint
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter
import pw.vintr.vintrless.domain.v2ray.interactor.V2rayInteractor
import pw.vintr.vintrless.presentation.navigation.BottomSheetNavigator
import pw.vintr.vintrless.v2ray.CupertinoV2rayInteractor

class IOSBottomSheetNavigator(sheetState: ModalBottomSheetState) : BottomSheetNavigator(sheetState)

actual fun getBottomSheetNavigator(
    sheetState: ModalBottomSheetState
): BottomSheetNavigator = IOSBottomSheetNavigator(sheetState)

@Composable
actual fun applyThemeOnView(darkTheme: Boolean) {}

actual fun NativePaint.setMaskFilter(blurRadius: Float) {
    this.maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, blurRadius / 2, true)
}

@OptIn(ExperimentalSettingsApi::class)
actual fun FlowSettings() = NSUserDefaultsSettings.Factory().create().toFlowSettings()

actual fun V2rayInteractor(): V2rayInteractor = CupertinoV2rayInteractor
