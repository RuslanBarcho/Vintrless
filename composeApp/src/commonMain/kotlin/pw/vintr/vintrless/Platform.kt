package pw.vintr.vintrless

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.NativePaint
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayPlatformInteractor
import pw.vintr.vintrless.platform.PlatformType
import pw.vintr.vintrless.presentation.navigation.BottomSheetNavigator

expect fun getBottomSheetNavigator(sheetState: ModalBottomSheetState): BottomSheetNavigator

@Composable
expect fun applyThemeOnView(darkTheme: Boolean)

@Composable
expect fun LazyColumnScrollbar(modifier: Modifier = Modifier, listState: LazyListState)

expect fun NativePaint.setMaskFilter(blurRadius: Float)

@OptIn(ExperimentalSettingsApi::class)
expect fun FlowSettings(): FlowSettings

expect fun V2RayPlatformInteractor(): V2RayPlatformInteractor

expect fun cameraAvailable(): Boolean

expect fun platformType(): PlatformType
