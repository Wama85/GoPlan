package com.softwama.goplan.core.notifications.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.softwama.goplan.core.notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.tasks.await

class NotificationRepositoryImpl(
    private val firebaseMessaging: FirebaseMessaging
    // private val apiService: ApiService // Cuando tengas tu API
) : NotificationRepository {

    override suspend fun getFcmToken(): Result<String> {
        return try {
            val token = firebaseMessaging.token.await()
            Log.d(TAG, "✅ Token FCM obtenido: $token")
            Result.success(token)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo token FCM", e)
            Result.failure(e)
        }
    }

    override suspend fun subscribeToTopic(topic: String): Result<Unit> {
        return try {
            firebaseMessaging.subscribeToTopic(topic).await()
            Log.d(TAG, "✅ Suscrito al tema: $topic")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error suscribiéndose al tema: $topic", e)
            Result.failure(e)
        }
    }

    override suspend fun unsubscribeFromTopic(topic: String): Result<Unit> {
        return try {
            firebaseMessaging.unsubscribeFromTopic(topic).await()
            Log.d(TAG, "✅ Desuscrito del tema: $topic")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error desuscribiéndose del tema: $topic", e)
            Result.failure(e)
        }
    }

    override suspend fun sendTokenToBackend(token: String): Result<Unit> {
        return try {
            // TODO: Implementar cuando tengas tu API
            Log.d(TAG, "📤 Enviando token al backend: $token")
            // apiService.updateFcmToken(token)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error enviando token al backend", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "NotificationRepository"
    }
}