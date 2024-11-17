package pw.vintr.vintrless

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.NativePaint
import pw.vintr.vintrless.presentation.navigation.BottomSheetNavigator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getBottomSheetNavigator(sheetState: ModalBottomSheetState): BottomSheetNavigator

@Composable
expect fun applyThemeOnView(darkTheme: Boolean)

expect fun NativePaint.setMaskFilter(blurRadius: Float)
