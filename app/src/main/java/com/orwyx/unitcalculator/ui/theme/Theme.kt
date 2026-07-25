package com.orwyx.unitcalculator.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color
import com.orwyx.unitcalculator.domain.model.AccentColor
import com.orwyx.unitcalculator.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = Blue500,
    onPrimary = Color_White,
    primaryContainer = BlueTint,
    onPrimaryContainer = Blue700,
    secondary = Blue400,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOnSurfaceVariant,
)

private val DarkColors = darkColorScheme(
    primary = Blue400,
    onPrimary = Color_White,
    primaryContainer = Blue700,
    onPrimaryContainer = BlueTint,
    secondary = Blue500,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOnSurfaceVariant,
)

/** Exposes the neumorphic shadow/highlight pair for the active theme to composables. */
data class NeuColors(
    val shadow: androidx.compose.ui.graphics.Color,
    val highlight: androidx.compose.ui.graphics.Color,
    val isDark: Boolean,
)

val LocalNeuColors = staticCompositionLocalOf {
    NeuColors(NeuLightShadow, NeuLightHighlight, isDark = false)
}

private fun accentPair(accent: AccentColor): Pair<Color, Color> = when (accent) {
    AccentColor.BLUE   -> Blue500 to Blue400
    AccentColor.PURPLE -> Color(0xFF7C4DFF) to Color(0xFF9575CD)
    AccentColor.TEAL   -> Color(0xFF0097A7) to Color(0xFF4DD0E1)
    AccentColor.GREEN  -> Color(0xFF00897B) to Color(0xFF4DB6AC)
    AccentColor.ORANGE -> Color(0xFFE65100) to Color(0xFFFFAB40)
    AccentColor.PINK   -> Color(0xFFD81B60) to Color(0xFFF48FB1)
}

@Composable
fun UnitCalculatorTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentColor: AccentColor = AccentColor.BLUE,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val (primary500, primary400) = accentPair(accentColor)
    val colors = if (dark) DarkColors.copy(primary = primary400, secondary = primary500)
    else LightColors.copy(primary = primary500, secondary = primary400)
    val neu = if (dark) NeuColors(NeuDarkShadow, NeuDarkHighlight, true)
    else NeuColors(NeuLightShadow, NeuLightHighlight, false)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !dark
            controller.isAppearanceLightNavigationBars = !dark
        }
    }

    CompositionLocalProvider(LocalNeuColors provides neu) {
        MaterialTheme(
            colorScheme = colors,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
