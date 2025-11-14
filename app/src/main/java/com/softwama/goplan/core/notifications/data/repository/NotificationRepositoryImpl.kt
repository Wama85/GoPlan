package com.softwama.goplan.core.notifications.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.softwama.goplan.core.notifications.domain.repository.NotificationRepository
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import kotlinx.coroutines.tasks.await

class NotificationRepositoryImpl(
    private val firebaseMessaging: FirebaseMessaging,
    private val dataStore: UserPreferencesDataStore
) : NotificationRepository {

    override suspend fun getFcmToken(): Result<String> {
        return try {
            val token = firebaseMessaging.token.await()
            Log.d(TAG, "✅ Token FCM obtenido: $token")
            dataStore.saveFcmToken(token)
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
        Log.d(TAG, "ℹ️ Solo Firebase - Token guardado localmente")
        return Result.success(Unit)
    }

    companion object {
        private const val TAG = "NotificationRepository"
    }
}