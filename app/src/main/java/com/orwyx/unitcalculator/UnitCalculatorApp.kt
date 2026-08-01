package com.orwyx.unitcalculator

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.orwyx.unitcalculator.core.notification.MeterAlarmScheduler
import com.orwyx.unitcalculator.core.notification.NotificationChannels
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class UnitCalculatorApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.createAll(this)
        // Schedule a daily 12PM AlarmManager alarm (survives Doze mode; rescheduled on boot)
        MeterAlarmScheduler.schedule(this)
    }
}
