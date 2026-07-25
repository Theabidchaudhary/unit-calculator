package com.orwyx.unitcalculator.di

import com.orwyx.unitcalculator.data.repository.HistoryRepositoryImpl
import com.orwyx.unitcalculator.data.repository.MeterRepositoryImpl
import com.orwyx.unitcalculator.data.repository.SettingsRepositoryImpl
import com.orwyx.unitcalculator.domain.repository.HistoryRepository
import com.orwyx.unitcalculator.domain.repository.MeterRepository
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMeterRepository(impl: MeterRepositoryImpl): MeterRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
