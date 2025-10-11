package com.softwama.goplan.core.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore

import kotlinx.coroutines.tasks.await

class FirebaseNotificationManager(
    private val firebaseMessaging: FirebaseMessaging,
    private val dataStore: UserPreferencesDataStore
) {

    companion object {
        private const val TAG = "FCMManager"
    }

    /**
     * Obtener el token FCM actual
     */
    suspend fun getFcmToken(): Result<String> {
        return try {
            val token = firebaseMessaging.token.await()
            Log.d(TAG, "✅ Token FCM obtenido: $token")

            // Guardar el token en DataStore
            dataStore.saveFcmToken(token)

            Result.success(token)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al obtener token FCM", e)
            Result.failure(e)
        }
    }

    /**
     * Suscribirse a un tema de notificaciones
     */
    suspend fun subscribeToTopic(topic: String): Result<Unit> {
        return try {
            firebaseMessaging.subscribeToTopic(topic).await()
            Log.d(TAG, "✅ Suscrito al tema: $topic")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al suscribirse al tema: $topic", e)
            Result.failure(e)
        }
    }

    /**
     * Desuscribirse de un tema de notificaciones
     */
    suspend fun unsubscribeFromTopic(topic: String): Result<Unit> {
        return try {
            firebaseMessaging.unsubscribeFromTopic(topic).await()
            Log.d(TAG, "✅ Desuscrito del tema: $topic")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al desuscribirse del tema: $topic", e)
            Result.failure(e)
        }
    }
}

