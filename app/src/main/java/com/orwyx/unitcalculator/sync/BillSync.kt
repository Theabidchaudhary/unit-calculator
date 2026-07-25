package com.orwyx.unitcalculator.sync

/** Bill data retrieved from a provider, mapped onto the app's meter/history fields. */
data class BillInfo(
    val billMonth: String,
    val readingDate: String?,
    val previousReading: Double?,
    val currentReading: Double?,
    val unitsConsumed: Double?,
    val billAmount: Double?,
)

sealed interface BillSyncResult {
    data class Success(val bill: BillInfo) : BillSyncResult
    /** The provider has no public, login-free lookup — the app carries on offline. */
    data object Unsupported : BillSyncResult
    data class Error(val message: String) : BillSyncResult
}

/**
 * A single electricity company's online bill lookup. Each provider is a self-contained adapter so
 * companies can be added or updated independently without touching the rest of the app. Register
 * concrete implementations in [SyncModule]; the app works fully offline when none are present.
 */
interface BillProvider {
    val providerId: String
    suspend fun fetchBill(referenceNumber: String): BillSyncResult
}

/**
 * Routes a sync request to the matching [BillProvider]. Returns [BillSyncResult.Unsupported] when
 * no adapter is registered for the provider, so the caller can show an informative message and
 * continue. This is the only entry point the UI depends on.
 */
class BillSyncEngine(private val providers: Set<BillProvider>) {

    fun isSupported(providerId: String): Boolean =
        providers.any { it.providerId == providerId }

    suspend fun sync(providerId: String, referenceNumber: String): BillSyncResult {
        val provider = providers.firstOrNull { it.providerId == providerId }
            ?: return BillSyncResult.Unsupported
        return runCatching { provider.fetchBill(referenceNumber) }
            .getOrElse { BillSyncResult.Error(it.message ?: "Sync failed. Please try again later.") }
    }
}
