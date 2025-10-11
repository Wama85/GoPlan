package com.softwama.goplan.core.notifications.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.softwama.goplan.core.notifications.NotificationHelper
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore

import com.softwama.goplan.features.calendar.data.CalendarRepositoryImpl
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class EventNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dataStore = UserPreferencesDataStore(applicationContext)

        // ✅ Verificar si las notificaciones están habilitadas - agregar .first()
        val notificationsEnabled = dataStore.getNotificationsEnabled().first()
        if (!notificationsEnabled) {
            return Result.success()
        }

        // ✅ Obtener el tiempo de anticipación configurado - agregar .first()
        val notificationTimeMinutes = dataStore.getNotificationTime().first().toLongOrNull() ?: 30L

        // Obtener eventos próximos
        val repository = CalendarRepositoryImpl(applicationContext)
        val events = repository.getEvents().first()

        val notificationHelper = NotificationHelper(applicationContext)
        val now = LocalDateTime.now()

        // Revisar cada evento
        events.forEach { event ->
            try {
                val eventTime = LocalDateTime.parse(event.startTime)
                val minutesUntil = ChronoUnit.MINUTES.between(now, eventTime)

                // Si el evento está dentro del rango de notificación
                if (minutesUntil in 0..notificationTimeMinutes) {
                    val timeUntilText = when {
                        minutesUntil < 1 -> "Ahora"
                        minutesUntil < 60 -> "En $minutesUntil minutos"
                        else -> {
                            val hours = minutesUntil / 60
                            "En $hours hora${if (hours > 1) "s" else ""}"
                        }
                    }

                    notificationHelper.showEventNotification(
                        eventId = event.id,
                        title = event.title,
                        description = event.description,
                        timeUntilEvent = timeUntilText
                    )
                }
            } catch (e: Exception) {
                // Error al parsear la fecha, continuar con el siguiente evento
            }
        }

        return Result.success()
    }
}