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

// Glass surface colors used by M3 for dialogs, sheets, menus
private val GlassDarkSurface         = Color(0xFF1A1F30)
private val GlassDarkSurfaceVariant  = Color(0xFF232840)
private val GlassDarkOnSurface       = Color(0xFFEDF0FF)
private val GlassDarkOnSurfaceVar    = Color(0xFF9CA3C0)

private val GlassLightSurface        = Color(0xFFF3F6FF)
private val GlassLightSurfaceVariant = Color(0xFFE3E8F8)
private val GlassLightOnSurface      = Color(0xFF0F1128)
private val GlassLightOnSurfaceVar   = Color(0xFF4A5380)

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
    val data    = themeData(appTheme)
    val primary = if (dark) data.accentDark else data.accentLight

    val colors = if (dark) {
        darkColorScheme(
            primary             = primary,
            onPrimary           = Color.White,
            primaryContainer    = primary.copy(alpha = 0.22f),
            onPrimaryContainer  = Color.White,
            secondary           = data.accentLight,
            background          = data.bgDark,
            onBackground        = GlassDarkOnSurface,
            surface             = GlassDarkSurface,
            onSurface           = GlassDarkOnSurface,
            surfaceVariant      = GlassDarkSurfaceVariant,
            onSurfaceVariant    = GlassDarkOnSurfaceVar,
            outline             = GlassDarkOnSurfaceVar.copy(alpha = 0.45f),
        )
    } else {
        lightColorScheme(
            primary             = primary,
            onPrimary           = Color.White,
            primaryContainer    = primary.copy(alpha = 0.14f),
            onPrimaryContainer  = primary,
            secondary           = data.accentDark,
            background          = data.bgLight,
            onBackground        = GlassLightOnSurface,
            surface             = GlassLightSurface,
            onSurface           = GlassLightOnSurface,
            surfaceVariant      = GlassLightSurfaceVariant,
            onSurfaceVariant    = GlassLightOnSurfaceVar,
            outline             = GlassLightOnSurfaceVar.copy(alpha = 0.45f),
        )
    }

    val neu = if (dark)
        NeuColors(Color.Black.copy(alpha = 0.55f), Color.White.copy(alpha = 0.08f), true)
    else
        NeuColors(Color.Black.copy(alpha = 0.14f), Color.White.copy(alpha = 0.85f), false)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !dark
            controller.isAppearanceLightNavigationBars = !dark
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
