package com.orwyx.unitcalculator

import com.orwyx.unitcalculator.domain.engine.MeterValidator
import com.orwyx.unitcalculator.domain.model.MeterInput
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MeterValidatorTest {

    private val validator = MeterValidator()

    private val valid = MeterInput(
        name = "House",
        referenceNumber = "0100489024827",
        targetLimit = "200",
        previousReading = "1000",
        currentReading = "1100",
    )

    @Test
    fun `valid input passes`() {
        assertTrue(validator.validate(valid, allowDecimals = false).isValid)
    }

    @Test
    fun `blank name fails`() {
        assertNotNull(validator.validate(valid.copy(name = " "), false).name)
    }

    @Test
    fun `non-digit reference fails`() {
        assertNotNull(validator.validate(valid.copy(referenceNumber = "12A45"), false).referenceNumber)
    }

    @Test
    fun `zero target fails`() {
        assertNotNull(validator.validate(valid.copy(targetLimit = "0"), false).targetLimit)
    }

    @Test
    fun `current less than previous fails`() {
        val errors = validator.validate(valid.copy(currentReading = "900"), false)
        assertNotNull(errors.currentReading)
    }

    @Test
    fun `negative numbers fail`() {
        assertNotNull(validator.validate(valid.copy(previousReading = "-5"), false).previousReading)
    }

    @Test
    fun `decimals rejected when disabled but accepted when enabled`() {
        val withDecimal = valid.copy(currentReading = "1100.5")
        assertNotNull(validator.validate(withDecimal, allowDecimals = false).currentReading)
        assertNull(validator.validate(withDecimal, allowDecimals = true).currentReading)
    }

    @Test
    fun `equal current and previous is allowed`() {
        val errors = validator.validate(valid.copy(previousReading = "1000", currentReading = "1000"), false)
        assertFalse(errors.currentReading != null)
    }
}
