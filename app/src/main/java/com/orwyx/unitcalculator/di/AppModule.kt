package com.orwyx.unitcalculator.di

import android.content.Context
import com.orwyx.unitcalculator.core.notification.MeterAlertNotifier
import com.orwyx.unitcalculator.data.prefs.SettingsDataStore
import com.orwyx.unitcalculator.domain.engine.CalculationEngine
import com.orwyx.unitcalculator.domain.engine.ForecastEngine
import com.orwyx.unitcalculator.domain.engine.MeterValidator
import com.orwyx.unitcalculator.domain.engine.PlanningEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore =
        SettingsDataStore(context)

    @Provides
    @Singleton
    fun provideCalculationEngine(): CalculationEngine = CalculationEngine()

    @Provides
    @Singleton
    fun provideMeterValidator(): MeterValidator = MeterValidator()

    @Provides
    @Singleton
    fun provideForecastEngine(): ForecastEngine = ForecastEngine()

    @Provides
    @Singleton
    fun providePlanningEngine(): PlanningEngine = PlanningEngine()

    @Provides
    @Singleton
    fun provideMeterAlertNotifier(@ApplicationContext context: Context): MeterAlertNotifier =
        MeterAlertNotifier(context)
}
