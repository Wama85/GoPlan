package com.softwama.goplan.core.notifications.domain.usecase

import android.util.Log
import com.softwama.goplan.core.notifications.domain.model.NotificationData

class HandleNotificationUseCase {

    operator fun invoke(data: NotificationData): String? {
        Log.d("HandleNotification", "Procesando notificación: ${data.type}")

        return when (data.type) {
            "task_reminder", "task_assigned" -> {
                data.taskId?.let { "task/$it" } ?: "dashboard"
            }
            "event_reminder", "calendar_event" -> {
                data.eventId?.let { "calendar?eventId=$it" } ?: "calendar"
            }
            else -> {
                data.navigateTo ?: "dashboard"
            }
        }
    }
}