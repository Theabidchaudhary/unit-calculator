package com.orwyx.unitcalculator.domain.model

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val readingDate: Int = 1,
    val defaultTarget: Double = 180.0,
    val allowDecimals: Boolean = true,
    val activeMeterId: Long? = null,
    val accentColor: AccentColor = AccentColor.BLUE,
    val meterColors: Map<Long, Int> = emptyMap(), // meterId -> pickerColors index
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }

enum class AccentColor(val displayName: String) {
    BLUE("Blue"),
    NAVY("Navy"),
    INDIGO("Indigo"),
    DEEP_PURPLE("Deep Purple"),
    PURPLE("Purple"),
    VIOLET("Violet"),
    MAGENTA("Magenta"),
    PINK("Pink"),
    ROSE("Rose"),
    RED("Red"),
    DEEP_ORANGE("Deep Orange"),
    ORANGE("Orange"),
    AMBER("Amber"),
    LIME("Lime"),
    GREEN("Green"),
    EMERALD("Emerald"),
    TEAL("Teal"),
    CYAN("Cyan"),
    BROWN("Brown"),
    SLATE("Slate"),
}
