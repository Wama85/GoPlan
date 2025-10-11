package com.softwama.goplan.core.notifications.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.softwama.goplan.MainActivity
import com.softwama.goplan.R
import com.softwama.goplan.core.notifications.domain.model.NotificationData
import com.softwama.goplan.core.notifications.domain.repository.NotificationRepository
import com.softwama.goplan.core.notifications.domain.usecase.HandleNotificationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationRepository: NotificationRepository by inject()
    private val handleNotificationUseCase: HandleNotificationUseCase by inject()

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "🔄 Nuevo token FCM generado: $token")

        // Enviar token al backend usando el repositorio
        serviceScope.launch {
            notificationRepository.sendTokenToBackend(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "📨 Mensaje recibido de: ${message.from}")

        // Convertir RemoteMessage a NotificationData (nuestro modelo de dominio)
        val notificationData = message.toNotificationData()

        // Usar el UseCase para determinar la navegación
        val navigationRoute = handleNotificationUseCase(notificationData)

        // Mostrar notificación
        showNotification(notificationData, navigationRoute)
    }

    private fun RemoteMessage.toNotificationData(): NotificationData {
        return NotificationData(
            title = notification?.title ?: data["title"],
            body = notification?.body ?: data["body"],
            navigateTo = data["screen"] ?: data["navigate_to"],
            taskId = data["taskId"],
            eventId = data["eventId"],
            type = data["type"],
            extras = data
        )
    }

    private fun showNotification(data: NotificationData, navigationRoute: String?) {
        val channelId = getString(R.string.default_notification_channel_id)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación
        createNotificationChannel(notificationManager, channelId)

        // Intent para abrir la app y navegar
        val intent = createNavigationIntent(navigationRoute, data)
        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Construir notificación
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(data.title ?: "GoPlan")
            .setContentText(data.body ?: "Nueva notificación")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

        Log.d(TAG, "✅ Notificación mostrada - Navegación: $navigationRoute")
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        channelId: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "GoPlan Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de tareas y recordatorios"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNavigationIntent(
        navigationRoute: String?,
        data: NotificationData
    ): Intent {
        return Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // Agregar datos de navegación
            navigationRoute?.let { putExtra("navigate_to", it) }
            data.taskId?.let { putExtra("taskId", it) }
            data.eventId?.let { putExtra("eventId", it) }
            data.type?.let { putExtra("type", it) }
        }
    }

    companion object {
        private const val TAG = "FCMService"
    }
}