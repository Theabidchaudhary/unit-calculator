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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.orwyx.unitcalculator.domain.model.AccentColor
import com.orwyx.unitcalculator.domain.model.ThemeMode

data class NeuColors(
    val shadow: Color,
    val highlight: Color,
    val isDark: Boolean,
)

val LocalNeuColors = staticCompositionLocalOf {
    NeuColors(NeuLightShadow, NeuLightHighlight, isDark = false)
}

private fun accentPair(accent: AccentColor): Pair<Color, Color> = when (accent) {
    AccentColor.BLUE         -> Color(0xFF3A5BFF) to Color(0xFF5B7CFA)
    AccentColor.NAVY         -> Color(0xFF0D47A1) to Color(0xFF1976D2)
    AccentColor.INDIGO       -> Color(0xFF3949AB) to Color(0xFF7986CB)
    AccentColor.DEEP_PURPLE  -> Color(0xFF512DA8) to Color(0xFF9575CD)
    AccentColor.PURPLE       -> Color(0xFF7C4DFF) to Color(0xFF9575CD)
    AccentColor.VIOLET       -> Color(0xFF7B1FA2) to Color(0xFFBA68C8)
    AccentColor.MAGENTA      -> Color(0xFF880E4F) to Color(0xFFF06292)
    AccentColor.PINK         -> Color(0xFFD81B60) to Color(0xFFF48FB1)
    AccentColor.ROSE         -> Color(0xFFE91E63) to Color(0xFFF48FB1)
    AccentColor.RED          -> Color(0xFFC62828) to Color(0xFFEF5350)
    AccentColor.DEEP_ORANGE  -> Color(0xFFBF360C) to Color(0xFFFF7043)
    AccentColor.ORANGE       -> Color(0xFFE65100) to Color(0xFFFFAB40)
    AccentColor.AMBER        -> Color(0xFFFF6F00) to Color(0xFFFFCA28)
    AccentColor.LIME         -> Color(0xFF558B2F) to Color(0xFF8BC34A)
    AccentColor.GREEN        -> Color(0xFF00897B) to Color(0xFF4DB6AC)
    AccentColor.EMERALD      -> Color(0xFF1B5E20) to Color(0xFF4CAF50)
    AccentColor.TEAL         -> Color(0xFF0097A7) to Color(0xFF4DD0E1)
    AccentColor.CYAN         -> Color(0xFF006064) to Color(0xFF00BCD4)
    AccentColor.BROWN        -> Color(0xFF4E342E) to Color(0xFF795548)
    AccentColor.SLATE        -> Color(0xFF37474F) to Color(0xFF546E7A)
}

@Composable
fun UnitCalculatorTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentColor: AccentColor = AccentColor.BLUE,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.LIGHT  -> false
        ThemeMode.DARK   -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val (primary500, primary400) = accentPair(accentColor)

    val colors = if (dark) {
        darkColorScheme(
            primary              = primary400,
            onPrimary            = Color_White,
            primaryContainer     = primary400.copy(alpha = 0.22f),
            onPrimaryContainer   = Color_White,
            secondary            = primary500,
            onSecondary          = Color_White,
            background           = DarkBackground,
            onBackground         = DarkOnSurface,
            surface              = DarkSurface,
            onSurface            = DarkOnSurface,
            surfaceVariant       = DarkSurfaceVariant,
            onSurfaceVariant     = DarkOnSurfaceVariant,
            outline              = DarkOnSurfaceVariant,
            error                = Color(0xFFCF6679),
            onError              = Color_White,
        )
    } else {
        lightColorScheme(
            primary              = primary500,
            onPrimary            = Color_White,
            primaryContainer     = primary500.copy(alpha = 0.12f),
            onPrimaryContainer   = primary500,
            secondary            = primary400,
            onSecondary          = Color_White,
            background           = LightBackground,
            onBackground         = LightOnSurface,
            surface              = LightSurface,
            onSurface            = LightOnSurface,
            surfaceVariant       = LightSurfaceVariant,
            onSurfaceVariant     = LightOnSurfaceVariant,
            outline              = LightOnSurfaceVariant,
            error                = Color(0xFFB3261E),
            onError              = Color_White,
        )
    }

    val neu = if (dark) NeuColors(NeuDarkShadow, NeuDarkHighlight, isDark = true)
    else NeuColors(NeuLightShadow, NeuLightHighlight, isDark = false)

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
            typography  = AppTypography,
            shapes      = AppShapes,
            content     = content,
        )
    }
}
