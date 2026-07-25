package com.orwyx.unitcalculator.core.util

import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

/** Shared display formatting so numbers look consistent everywhere in the app. */
object Formatters {

    /** Formats units, dropping the decimal when the value is whole (e.g. "54" vs "54.3"). */
    fun units(value: Double): String {
        val rounded = value.roundToLong()
        return if (abs(value - rounded) < 0.05) rounded.toString()
        else String.format(Locale.US, "%.1f", value)
    }

    fun percent(fraction: Float): String =
        "${(fraction * 100f).roundToLong().coerceAtLeast(0)}%"

    /** Masks all but the last four digits: "0100489024827" -> "*********4827". */
    fun maskedReference(reference: String): String {
        if (reference.length <= 4) return reference
        val visible = reference.takeLast(4)
        return "*".repeat(reference.length - 4) + visible
    }

    fun currencyPkr(amount: Double): String =
        "Rs " + String.format(Locale.US, "%,.0f", amount)
}
