package com.orwyx.unitcalculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
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

@Composable
fun UnitCalculatorTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colors = if (dark) DarkColors else LightColors
    val neu = if (dark) NeuColors(NeuDarkShadow, NeuDarkHighlight, true)
    else NeuColors(NeuLightShadow, NeuLightHighlight, false)

    CompositionLocalProvider(LocalNeuColors provides neu) {
        MaterialTheme(
            colorScheme = colors,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
