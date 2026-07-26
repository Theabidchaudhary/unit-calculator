package com.orwyx.unitcalculator.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.orwyx.unitcalculator.domain.model.AppTheme
import com.orwyx.unitcalculator.domain.model.ThemeMode

data class AppThemeData(
    val blob1: Color,
    val blob2: Color,
    val blob3: Color,
    val accentLight: Color,
    val accentDark: Color,
    val bgDark: Color,
    val bgLight: Color,
)

val LocalAppTheme = staticCompositionLocalOf { themeData(AppTheme.SUNSET) }

fun themeData(theme: AppTheme): AppThemeData = when (theme) {
    AppTheme.SUNSET   -> AppThemeData(Color(0xFFFF6B1A), Color(0xFFFFD166), Color(0xFFFF4081), Color(0xFFE65100), Color(0xFFFFAB40), Color(0xFF180500), Color(0xFFFFF5EC))
    AppTheme.OCEAN    -> AppThemeData(Color(0xFF0077B6), Color(0xFF00B4D8), Color(0xFF90E0EF), Color(0xFF0097A7), Color(0xFF4DD0E1), Color(0xFF000D1A), Color(0xFFECF8FC))
    AppTheme.DUSK     -> AppThemeData(Color(0xFF7C3AED), Color(0xFFEC4899), Color(0xFFC4B5FD), Color(0xFF7C4DFF), Color(0xFFB39DDB), Color(0xFF0D0020), Color(0xFFF5F0FF))
    AppTheme.FOREST   -> AppThemeData(Color(0xFF166534), Color(0xFF4ADE80), Color(0xFFBBF7D0), Color(0xFF00897B), Color(0xFF4DB6AC), Color(0xFF021A0A), Color(0xFFF0FFF4))
    AppTheme.ROSE     -> AppThemeData(Color(0xFFBE123C), Color(0xFFFB7185), Color(0xFFFECDD3), Color(0xFFD81B60), Color(0xFFF48FB1), Color(0xFF1A000D), Color(0xFFFFF0F5))
    AppTheme.MIDNIGHT -> AppThemeData(Color(0xFF1E3A5F), Color(0xFF2563EB), Color(0xFF93C5FD), Color(0xFF3A5BFF), Color(0xFF5B7CFA), Color(0xFF020B18), Color(0xFFEEF2FF))
    AppTheme.LAVA     -> AppThemeData(Color(0xFF7F1D1D), Color(0xFFC2410C), Color(0xFFF97316), Color(0xFFC62828), Color(0xFFFF7043), Color(0xFF1A0000), Color(0xFFFFF3EE))
    AppTheme.ARCTIC   -> AppThemeData(Color(0xFFBAE6FD), Color(0xFFE0F2FE), Color(0xFF7DD3FC), Color(0xFF37474F), Color(0xFF546E7A), Color(0xFF0A1520), Color(0xFFF0F9FF))
    AppTheme.GOLDEN   -> AppThemeData(Color(0xFFD97706), Color(0xFFFCD34D), Color(0xFFFEF3C7), Color(0xFFFF6F00), Color(0xFFFFCA28), Color(0xFF1A0F00), Color(0xFFFFFBEC))
    AppTheme.COSMIC   -> AppThemeData(Color(0xFF4C1D95), Color(0xFFA855F7), Color(0xFFEC4899), Color(0xFF512DA8), Color(0xFFCE93D8), Color(0xFF0D0020), Color(0xFFF5F0FF))
}

data class NeuColors(
    val shadow: Color,
    val highlight: Color,
    val isDark: Boolean,
)

val LocalNeuColors = staticCompositionLocalOf {
    NeuColors(NeuLightShadow, NeuLightHighlight, isDark = false)
}

// Warm accent that matches the room photo lighting
private val WarmOrange = Color(0xFFE87C1A)
private val WarmAmber  = Color(0xFFFF9933)

// Always-white text on any background — the photo background is always dark-ish
private val AlwaysWhite      = Color.White
private val AlwaysWhiteDim   = Color.White.copy(alpha = 0.65f)
private val AlwaysWhiteFaint = Color.White.copy(alpha = 0.40f)

// Glass surface — used only for M3 internal dialogs/sheets (not our custom cards)
private val GlassSurface = Color(0xFF2A1508)

@Composable
fun UnitCalculatorTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    appTheme: AppTheme = AppTheme.SUNSET,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.LIGHT  -> false
        ThemeMode.DARK   -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // Both schemes use white text — photo background is always warm-dark
    val colors = darkColorScheme(
        primary              = if (dark) WarmAmber else WarmOrange,
        onPrimary            = AlwaysWhite,
        primaryContainer     = WarmOrange.copy(alpha = 0.28f),
        onPrimaryContainer   = AlwaysWhite,
        secondary            = WarmAmber,
        onSecondary          = AlwaysWhite,
        background           = Color(0xFF1A0800),
        onBackground         = AlwaysWhite,
        surface              = GlassSurface,
        onSurface            = AlwaysWhite,
        surfaceVariant       = GlassSurface.copy(alpha = 0.60f),
        onSurfaceVariant     = AlwaysWhiteDim,
        outline              = AlwaysWhiteFaint,
        error                = Color(0xFFFF6B6B),
        onError              = AlwaysWhite,
        inverseSurface       = Color(0xFFF5E6D3),
        inverseOnSurface     = Color(0xFF1A0800),
    )

    val data = themeData(appTheme)
    val neu  = NeuColors(Color.Black.copy(alpha = 0.55f), Color.White.copy(alpha = 0.08f), dark)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            // Always light-on-dark status bar icons since background is warm-dark
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    CompositionLocalProvider(
        LocalNeuColors provides neu,
        LocalAppTheme provides data,
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography  = AppTypography,
            shapes      = AppShapes,
            content     = content,
        )
    }
}
