package pw.vintr.vintrless

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import okio.Path.Companion.toPath
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayPlatformInteractor
import pw.vintr.vintrless.platform.PlatformType
import pw.vintr.vintrless.presentation.navigation.BottomSheetNavigator
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.tools.PathProvider
import pw.vintr.vintrless.v2ray.interactor.JvmV2RayInteractor

class DesktopBottomSheetNavigator(sheetState: ModalBottomSheetState) : BottomSheetNavigator(sheetState)

actual fun getBottomSheetNavigator(
    sheetState: ModalBottomSheetState
): BottomSheetNavigator = DesktopBottomSheetNavigator(sheetState)

@Composable
actual fun applyThemeOnView(darkTheme: Boolean) {}

@Composable
actual fun LazyColumnScrollbar(
    modifier: Modifier,
    listState: LazyListState
) {
    VerticalScrollbar(
        modifier = modifier.fillMaxHeight(),
        adapter = rememberScrollbarAdapter(listState),
        style = ScrollbarStyle(
            unhoverColor = VintrlessExtendedTheme.colors.scrollbarUnhover,
            hoverColor = VintrlessExtendedTheme.colors.scrollbarHover,
            minimalHeight = 24.dp,
            thickness = 14.dp,
            shape = RoundedCornerShape(12.dp),
            hoverDurationMillis = 300,
        )
    )
}

actual fun NativePaint.setMaskFilter(blurRadius: Float) {
    this.maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, blurRadius / 2, true)
}

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
actual fun FlowSettings(): FlowSettings {
    val dataStore = PreferenceDataStoreFactory.createWithPath(
        produceFile = { PathProvider.dataStoreFilePath.toPath() }
    )
    return DataStoreSettings(dataStore)
}

actual fun V2RayPlatformInteractor(): V2RayPlatformInteractor = JvmV2RayInteractor

actual fun cameraAvailable() = false

actual fun platformType() = PlatformType.JVM
