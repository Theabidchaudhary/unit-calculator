package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ElectricMeter
import androidx.compose.ui.graphics.vector.ImageVector

/** Central route table. Detail/edit routes carry the meter id as a path/optional argument. */
object Routes {
    const val METERS = "meters"
    const val PLANNING = "planning"
    const val SETTINGS = "settings"
    const val SEARCH = "search"
    const val HISTORY = "history"

    const val METER_DETAIL = "meter_detail/{meterId}"
    const val METER_EDIT = "meter_edit?meterId={meterId}"

    fun meterDetail(id: Long) = "meter_detail/$id"
    fun meterEdit(id: Long? = null) = if (id == null) "meter_edit" else "meter_edit?meterId=$id"

    const val ARG_METER_ID = "meterId"
}

/** The two bottom-navigation destinations. */
enum class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    METERS(Routes.METERS, "Meters", Icons.Rounded.ElectricMeter),
    PLANNING(Routes.PLANNING, "Planning", Icons.Rounded.CalendarMonth),
}
