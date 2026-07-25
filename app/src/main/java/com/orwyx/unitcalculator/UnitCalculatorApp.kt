package com.orwyx.unitcalculator

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.orwyx.unitcalculator.core.notification.NotificationChannels
import com.orwyx.unitcalculator.core.worker.DailyMeterCheckWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
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
        scheduleDailyCheck()
    }

    private fun scheduleDailyCheck() {
        val request = PeriodicWorkRequestBuilder<DailyMeterCheckWorker>(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyMeterCheckWorker.WORK_NAME_DAILY,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
