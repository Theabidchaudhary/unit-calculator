package com.orwyx.unitcalculator.ocr

import android.net.Uri

/** Fields extracted from a scanned or imported bill, all optional pending user confirmation. */
data class ScannedBill(
    val referenceNumber: String? = null,
    val previousReading: Double? = null,
    val currentReading: Double? = null,
    val unitsConsumed: Double? = null,
    val readingDate: String? = null,
    val billAmount: Double? = null,
)

sealed interface ScanResult {
    data class Success(val bill: ScannedBill) : ScanResult
    /** OCR isn't wired up in this build; the user can still enter values manually. */
    data object NotAvailable : ScanResult
    data class Error(val message: String) : ScanResult
}

/**
 * Extracts bill fields from a photo or PDF. Isolated behind this interface so the OCR backend
 * (e.g. ML Kit text recognition) can be dropped in later without changing callers. The user always
 * confirms extracted values before they are saved.
 */
interface BillScanner {
    suspend fun scan(source: Uri): ScanResult
}

/** Default no-op scanner: keeps the app fully functional until a real OCR engine is added. */
class NoopBillScanner : BillScanner {
    override suspend fun scan(source: Uri): ScanResult = ScanResult.NotAvailable
}
