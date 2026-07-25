package com.orwyx.unitcalculator.domain.model

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val readingDate: Int = 1,
    val defaultTarget: Double = 200.0,
    val allowDecimals: Boolean = false,
    val activeMeterId: Long? = null,
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }
