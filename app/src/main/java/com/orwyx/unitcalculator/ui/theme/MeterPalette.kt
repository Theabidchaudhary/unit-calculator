package com.orwyx.unitcalculator.ui.theme

import androidx.compose.ui.graphics.Color

object MeterPalette {
    private val colors = listOf(
        Color(0xFF3A5BFF), Color(0xFF14A44D), Color(0xFFF5C518), Color(0xFFFF8A00),
        Color(0xFF8E44AD), Color(0xFF16A085), Color(0xFFE74C3C), Color(0xFF2C3E50),
    )
    fun colorFor(index: Int): Color = colors[(index % colors.size).coerceIn(0, colors.lastIndex)]
}
