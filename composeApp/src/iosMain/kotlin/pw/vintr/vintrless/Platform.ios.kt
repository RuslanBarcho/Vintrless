package pw.vintr.vintrless

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.NativePaint
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter
import pw.vintr.vintrless.domain.log.interactor.LogPlatformInteractor
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayPlatformInteractor
import pw.vintr.vintrless.log.CupertinoLogInteractor
import pw.vintr.vintrless.platform.model.DeviceOrientation
import pw.vintr.vintrless.platform.model.PlatformType
import pw.vintr.vintrless.presentation.navigation.BottomSheetNavigator
import pw.vintr.vintrless.v2ray.CupertinoV2rayInteractor

class IOSBottomSheetNavigator(sheetState: ModalBottomSheetState) : BottomSheetNavigator(sheetState)

actual fun getBottomSheetNavigator(
    sheetState: ModalBottomSheetState
): BottomSheetNavigator = IOSBottomSheetNavigator(sheetState)

@Composable
actual fun applyThemeOnView(darkTheme: Boolean) {}

@Composable
actual fun LazyColumnScrollbar(
    modifier: Modifier,
    listState: LazyListState
) {}

@Composable
actual fun resolveOrientation(): DeviceOrientation {
    return DeviceOrientation.PORTRAIT
}

actual fun NativePaint.setMaskFilter(blurRadius: Float) {
    this.maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, blurRadius / 2, true)
}

@OptIn(ExperimentalSettingsApi::class)
actual fun FlowSettings() = NSUserDefaultsSettings.Factory().create().toFlowSettings()

actual fun V2RayPlatformInteractor(): V2RayPlatformInteractor = CupertinoV2rayInteractor

actual fun LogPlatformInteractor(): LogPlatformInteractor = CupertinoLogInteractor()

actual fun cameraAvailable() = false

actual fun platformType() = PlatformType.IOS
