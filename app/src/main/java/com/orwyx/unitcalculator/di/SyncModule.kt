package com.orwyx.unitcalculator.di

import com.orwyx.unitcalculator.ocr.BillScanner
import com.orwyx.unitcalculator.ocr.NoopBillScanner
import com.orwyx.unitcalculator.sync.BillProvider
import com.orwyx.unitcalculator.sync.BillSyncEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import javax.inject.Singleton

/**
 * Wires the optional online modules. No [BillProvider] adapters are registered yet, so bill sync
 * gracefully reports "unsupported" and the app stays fully offline-capable. Adding a real provider
 * later is a single `@Binds @IntoSet` — no changes elsewhere.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Multibinds
    abstract fun billProviders(): Set<BillProvider>

    companion object {
        @Provides
        @Singleton
        fun provideBillSyncEngine(providers: Set<@JvmSuppressWildcards BillProvider>): BillSyncEngine =
            BillSyncEngine(providers)

        @Provides
        @Singleton
        fun provideBillScanner(): BillScanner = NoopBillScanner()
    }
}
