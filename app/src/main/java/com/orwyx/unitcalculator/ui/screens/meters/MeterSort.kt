package com.orwyx.unitcalculator.ui.screens.meters

import com.orwyx.unitcalculator.domain.model.Meter

enum class MeterSort(val label: String) {
    SEQUENCE("My order"),
    NAME("Name"),
    REMAINING("Remaining units"),
    HIGHEST_CONSUMPTION("Highest consumption"),
    DANGER_LEVEL("Danger level"),
    NEWEST("Newest"),
    OLDEST("Oldest");

    fun sort(meters: List<Meter>): List<Meter> = when (this) {
        SEQUENCE -> meters.sortedBy { it.sortOrder }
        NAME -> meters.sortedBy { it.name.lowercase() }
        REMAINING -> meters.sortedBy { it.remainingUnits }
        HIGHEST_CONSUMPTION -> meters.sortedByDescending { it.consumedUnits }
        DANGER_LEVEL -> meters.sortedByDescending { it.usedFraction }
        NEWEST -> meters.sortedByDescending { it.createdAt }
        OLDEST -> meters.sortedBy { it.createdAt }
    }
}

fun List<Meter>.filterByQuery(query: String): List<Meter> {
    if (query.isBlank()) return this
    val q = query.trim().lowercase()
    return filter {
        it.name.lowercase().contains(q) || it.referenceNumber.contains(q)
    }
}
