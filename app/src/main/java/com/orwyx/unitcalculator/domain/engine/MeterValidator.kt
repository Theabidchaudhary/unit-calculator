package com.orwyx.unitcalculator.domain.engine

import com.orwyx.unitcalculator.domain.model.MeterInput
import com.orwyx.unitcalculator.domain.model.MeterInputErrors

/**
 * Validates [MeterInput] with friendly, user-facing messages. Pure and testable.
 * Enforces the spec rules: current >= previous, target > 0, no negatives, optional decimals.
 */
class MeterValidator {

    fun validate(input: MeterInput, allowDecimals: Boolean): MeterInputErrors {
        val target = input.targetLimit.toDoubleOrNull()
        val previous = input.previousReading.toDoubleOrNull()
        val current = input.currentReading.toDoubleOrNull()

        return MeterInputErrors(
            name = if (input.name.isBlank()) "Please enter a meter name" else null,
            referenceNumber = when {
                input.referenceNumber.isBlank() -> "Reference number is required"
                !input.referenceNumber.all { it.isDigit() } -> "Digits only"
                else -> null
            },
            targetLimit = numberError(input.targetLimit, target, allowDecimals, mustBePositive = true)
                ?: if (target != null && target <= 0.0) "Target must be greater than zero" else null,
            previousReading = numberError(input.previousReading, previous, allowDecimals),
            currentReading = numberError(input.currentReading, current, allowDecimals)
                ?: if (current != null && previous != null && current < previous)
                    "Current cannot be less than previous" else null,
        )
    }

    private fun numberError(
        raw: String,
        parsed: Double?,
        allowDecimals: Boolean,
        mustBePositive: Boolean = false,
    ): String? = when {
        raw.isBlank() -> "Required"
        parsed == null -> "Enter a valid number"
        parsed < 0 -> "Cannot be negative"
        !allowDecimals && raw.contains('.') -> "Whole numbers only"
        mustBePositive && parsed == 0.0 -> "Must be greater than zero"
        else -> null
    }
}
