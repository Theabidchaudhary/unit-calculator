package com.orwyx.unitcalculator.domain.model

/**
 * Raw editable values for the add/edit form, kept separate from the persisted [Meter] so the UI
 * can hold in-progress (possibly invalid) text without corrupting stored data.
 */
data class MeterInput(
    val id: Long = 0L,
    val name: String = "",
    val referenceNumber: String = "",
    val providerId: String = ElectricityProvider.DEFAULT.id,
    val targetLimit: String = "",
    val previousReading: String = "",
    val currentReading: String = "",
)

/** Field-level validation errors for [MeterInput]. A null field means that field is valid. */
data class MeterInputErrors(
    val name: String? = null,
    val referenceNumber: String? = null,
    val targetLimit: String? = null,
    val previousReading: String? = null,
    val currentReading: String? = null,
) {
    val isValid: Boolean
        get() = name == null && referenceNumber == null && targetLimit == null &&
            previousReading == null && currentReading == null
}
