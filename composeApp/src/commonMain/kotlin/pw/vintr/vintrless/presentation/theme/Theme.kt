package pw.vintr.vintrless.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import pw.vintr.vintrless.applyThemeOnView

@Immutable
data class VintrlessColors(
    // Screen
    val screenBackgroundColor: Color = Color.Unspecified,
    // Card
    val cardBackgroundColor: Color = Color.Unspecified,
    val cardStrokeColor: Color = Color.Unspecified,
    // Text
    val textRegular: Color = Color.Unspecified,
    val textSecondary: Color = Color.Unspecified,
    val textLabel: Color = Color.Unspecified,
    // Text field
    val textFieldContent: Color = Color.Unspecified,
    val textFieldHint: Color = Color.Unspecified,
    // Nav bar
    val navBarSelected: Color = Color.Unspecified,
    val navBarUnselected: Color = Color.Unspecified,
    // Common
    val shadow: Color = Color.Unspecified,
    val lineSeparator: Color = Color.Unspecified,
    val ripple: Color = Color.Unspecified,
    // Switch
    val switchActiveThumbColor: Color = Color.Unspecified,
    val switchActiveBackgroundColor: Color = Color.Unspecified,
    val switchInactiveThumbColor: Color = Color.Unspecified,
    val switchInactiveBackgroundColor: Color = Color.Unspecified,
    // Regular button
    val regularButtonBackground: Color = Color.Unspecified,
    val regularButtonContent: Color = Color.Unspecified,
    val regularButtonDisabledBackground: Color = Color.Unspecified,
    val regularButtonDisabledContent: Color = Color.Unspecified,
    // Radio button
    val radioBackground: Color = Color.Unspecified,
    val radioStroke: Color = Color.Unspecified,
    val radioSelected: Color = Color.Unspecified,
)

val LocalVintrColors = staticCompositionLocalOf { VintrlessColors() }

val darkVintrColors = VintrlessColors(
    // Screen
    screenBackgroundColor = AppColor.CodGray,
    // Card
    cardBackgroundColor = AppColor.ChineseBlack,
    cardStrokeColor = AppColor.MineShaft,
    // Text
    textRegular = AppColor.BrightGray,
    textSecondary = AppColor.FrenchGray,
    textLabel = AppColor.Spray,
    // Text field
    textFieldContent = AppColor.White,
    textFieldHint = AppColor.Abbey,
    // Nav bar
    navBarSelected = AppColor.BrightTurquoise,
    navBarUnselected = AppColor.White,
    // Common
    shadow = AppColor.Black,
    lineSeparator = AppColor.MineShaft,
    ripple = AppColor.BrightTurquoise,
    // Switch
    switchActiveThumbColor = AppColor.MineShaft,
    switchActiveBackgroundColor = AppColor.AthensGray,
    switchInactiveBackgroundColor = AppColor.MineShaft,
    switchInactiveThumbColor = AppColor.AthensGray,
    // Regular button
    regularButtonBackground = AppColor.ChargedBlue,
    regularButtonContent = AppColor.White,
    regularButtonDisabledBackground = AppColor.MineShaft,
    regularButtonDisabledContent = AppColor.Waterloo,
    // Radio button
    radioBackground = AppColor.MineShaft,
    radioStroke = AppColor.Jumbo,
    radioSelected = AppColor.White,
)

val lightVintrColors = VintrlessColors(
    // Screen
    screenBackgroundColor = AppColor.Alabaster,
    // Card
    cardBackgroundColor = AppColor.AthensGray,
    cardStrokeColor = AppColor.GraySuit,
    // Text
    textRegular = AppColor.Black,
    textSecondary = AppColor.Waterloo,
    textLabel = AppColor.ShojinBlue,
    // Text field
    textFieldContent = AppColor.Black,
    textFieldHint = AppColor.GraySuit,
    // Nav bar
    navBarSelected = AppColor.ShojinBlue,
    navBarUnselected = AppColor.GunPowder,
    // Common
    shadow = AppColor.FrenchGray,
    lineSeparator = AppColor.GraySuit,
    ripple = AppColor.ShojinBlue,
    // Switch
    switchActiveThumbColor = AppColor.White,
    switchActiveBackgroundColor = AppColor.ChargedBlue,
    switchInactiveBackgroundColor = AppColor.White,
    switchInactiveThumbColor = AppColor.ChargedBlue,
    // Radio button
    radioBackground = AppColor.AthensGray,
    radioStroke = AppColor.SilverChalice,
    radioSelected = AppColor.Black,
)

private val darkColorScheme = darkColorScheme(
    primary = AppColor.BrightTurquoise,
    secondary = AppColor.Cyan,
    tertiary = AppColor.Cerulean,
    background = AppColor.CodGray,
)

private val lightColorScheme = lightColorScheme(
    primary = AppColor.ShojinBlue,
    secondary = AppColor.Cyan,
    tertiary = AppColor.Cerulean,
    background = AppColor.Alabaster,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VintrlessRippleConfiguration() = RippleConfiguration(
    color = VintrlessExtendedTheme.colors.ripple,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VintrlessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme
    } else {
        lightColorScheme
    }
    val vintrColors = if (darkTheme) {
        darkVintrColors
    } else {
        lightVintrColors
    }

    applyThemeOnView(darkTheme = darkTheme)

    CompositionLocalProvider(LocalVintrColors provides vintrColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
        ) {
            CompositionLocalProvider(
                LocalRippleConfiguration provides VintrlessRippleConfiguration(),
                content = content
            )
        }
    }
}

object VintrlessExtendedTheme {
    val colors: VintrlessColors
        @Composable
        get() = LocalVintrColors.current
}

@Composable
fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = VintrlessExtendedTheme.colors.switchActiveThumbColor,
    uncheckedThumbColor = VintrlessExtendedTheme.colors.switchInactiveThumbColor,
    checkedTrackColor = VintrlessExtendedTheme.colors.switchActiveBackgroundColor,
    uncheckedTrackColor = VintrlessExtendedTheme.colors.switchInactiveBackgroundColor,
    checkedBorderColor = VintrlessExtendedTheme.colors.switchActiveBackgroundColor,
    uncheckedBorderColor = VintrlessExtendedTheme.colors.switchActiveBackgroundColor,
    disabledCheckedThumbColor = VintrlessExtendedTheme.colors.switchActiveThumbColor
        .copy(alpha = 0.5f),
    disabledCheckedTrackColor = VintrlessExtendedTheme.colors.switchActiveBackgroundColor
        .copy(alpha = 0.5f),
    disabledUncheckedThumbColor = VintrlessExtendedTheme.colors.switchInactiveThumbColor
        .copy(alpha = 0.5f),
    disabledUncheckedTrackColor = VintrlessExtendedTheme.colors.switchInactiveBackgroundColor
        .copy(alpha = 0.5f),
)
