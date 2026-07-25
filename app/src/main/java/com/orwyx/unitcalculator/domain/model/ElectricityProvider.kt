package com.orwyx.unitcalculator.domain.model

/**
 * A Pakistani electricity distribution company. Stored by [id] on the meter so the registry
 * can grow without database migrations. Adding a new provider is a one-line change here.
 */
data class ElectricityProvider(
    val id: String,
    val shortName: String,
    val fullName: String,
) {
    companion object {
        val MEPCO = ElectricityProvider("MEPCO", "MEPCO", "Multan Electric Power Company")
        val LESCO = ElectricityProvider("LESCO", "LESCO", "Lahore Electric Supply Company")
        val FESCO = ElectricityProvider("FESCO", "FESCO", "Faisalabad Electric Supply Company")
        val GEPCO = ElectricityProvider("GEPCO", "GEPCO", "Gujranwala Electric Power Company")
        val IESCO = ElectricityProvider("IESCO", "IESCO", "Islamabad Electric Supply Company")
        val HESCO = ElectricityProvider("HESCO", "HESCO", "Hyderabad Electric Supply Company")
        val SEPCO = ElectricityProvider("SEPCO", "SEPCO", "Sukkur Electric Power Company")
        val PESCO = ElectricityProvider("PESCO", "PESCO", "Peshawar Electric Supply Company")
        val QESCO = ElectricityProvider("QESCO", "QESCO", "Quetta Electric Supply Company")
        val KE = ElectricityProvider("KE", "K-Electric", "K-Electric Limited")

        /** All known providers, in display order. Append here to support more companies. */
        val ALL: List<ElectricityProvider> =
            listOf(MEPCO, LESCO, FESCO, GEPCO, IESCO, HESCO, SEPCO, PESCO, QESCO, KE)

        val DEFAULT: ElectricityProvider = MEPCO

        fun fromId(id: String?): ElectricityProvider =
            ALL.firstOrNull { it.id == id } ?: DEFAULT
    }
}
