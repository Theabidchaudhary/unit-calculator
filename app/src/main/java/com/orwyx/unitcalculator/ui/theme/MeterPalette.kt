package com.orwyx.unitcalculator.ui.theme

import androidx.compose.ui.graphics.Color

object MeterPalette {
    val pickerColors = listOf(
        Color(0xFF3A5BFF), Color(0xFF14A44D), Color(0xFFF5C518), Color(0xFFFF8A00),
        Color(0xFF8E44AD), Color(0xFF16A085), Color(0xFFE74C3C), Color(0xFF2C3E50),
        Color(0xFF1ABC9C), Color(0xFFE67E22), Color(0xFF9B59B6), Color(0xFF27AE60),
        Color(0xFFD35400), Color(0xFF2980B9), Color(0xFFC0392B), Color(0xFF795548),
        Color(0xFF00BCD4), Color(0xFFF06292), Color(0xFF0D9488), Color(0xFF7C3AED),
        Color(0xFFFF5722),
    )
    fun colorFor(index: Int): Color = pickerColors[(index % pickerColors.size).coerceIn(0, pickerColors.lastIndex)]
    fun colorForIndex(colorIndex: Int): Color = pickerColors.getOrElse(colorIndex) { pickerColors[0] }
}
