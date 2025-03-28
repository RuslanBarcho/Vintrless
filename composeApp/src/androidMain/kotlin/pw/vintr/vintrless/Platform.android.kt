package pw.vintr.vintrless

import android.app.Activity
import android.content.res.Configuration
import android.graphics.BlurMaskFilter
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.Navigator
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import pw.vintr.vintrless.domain.log.interactor.LogPlatformInteractor
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayPlatformInteractor
import pw.vintr.vintrless.log.AndroidLogInteractor
import pw.vintr.vintrless.platform.model.DeviceOrientation
import pw.vintr.vintrless.platform.model.PlatformType
import pw.vintr.vintrless.presentation.navigation.BottomSheetNavigator
import pw.vintr.vintrless.tools.AppContext
import pw.vintr.vintrless.v2ray.interactor.AndroidV2RayInteractor

@Navigator.Name("AndroidBottomSheetNavigator")
class AndroidBottomSheetNavigator(sheetState: ModalBottomSheetState) : BottomSheetNavigator(sheetState)

actual fun getBottomSheetNavigator(
    sheetState: ModalBottomSheetState
): BottomSheetNavigator = AndroidBottomSheetNavigator(sheetState)

@Composable
actual fun applyThemeOnView(darkTheme: Boolean) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val windowsInsetsController = WindowCompat.getInsetsController(window, view)

            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            windowsInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowsInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

@Composable
actual fun LazyColumnScrollbar(
    modifier: Modifier,
    listState: LazyListState
) {}

@Composable
actual fun resolveOrientation(): DeviceOrientation {
    return when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            DeviceOrientation.LANDSCAPE
        }

        Configuration.ORIENTATION_PORTRAIT -> {
            DeviceOrientation.PORTRAIT
        }

        else -> {
            DeviceOrientation.PORTRAIT
        }
    }
}

actual fun NativePaint.setMaskFilter(blurRadius: Float) {
    maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
}

@OptIn(ExperimentalSettingsApi::class)
actual fun FlowSettings() = SharedPreferencesSettings.Factory(AppContext.get()).create().toFlowSettings()

actual fun V2RayPlatformInteractor(): V2RayPlatformInteractor = AndroidV2RayInteractor

actual fun LogPlatformInteractor(): LogPlatformInteractor = AndroidLogInteractor()

actual fun cameraAvailable() = true

actual fun platformType() = PlatformType.ANDROID
