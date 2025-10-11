package com.softwama.goplan.core.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.softwama.goplan.core.notifications.data.EventNotificationWorker
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    companion object {
        private const val WORK_NAME = "EventNotificationWork"
        private const val REPEAT_INTERVAL_MINUTES = 15L // Revisar cada 15 minutos
    }

    fun scheduleEventNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<EventNotificationWorker>(
            REPEAT_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Mantener si ya existe
            workRequest
        )
    }

    fun cancelEventNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}