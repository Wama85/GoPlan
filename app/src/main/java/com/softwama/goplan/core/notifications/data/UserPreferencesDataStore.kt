package com.softwama.goplan.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.softwama.goplan.core.notifications.domain.usecase.NotificationPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesDataStore(private val context: Context) {

    private val Context.dataStore by preferencesDataStore("user_preferences")

    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

        // ✅ Nuevas claves para notificaciones
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
    }

    suspend fun saveSession(token: String, userName: String, userEmail: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.TOKEN] = token
            preferences[Keys.USERNAME] = userName
            preferences[Keys.EMAIL] = userEmail
            preferences[Keys.IS_LOGGED_IN] = true
        }
    }

    fun getUserToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.TOKEN]
        }
    }

    fun getUserName(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.USERNAME]
        }
    }

    fun getUserEmail(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.EMAIL]
        }
    }

    fun getLoginStatus(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.IS_LOGGED_IN] ?: false
        }
    }

    suspend fun saveLoginStatus(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // ✅ Habilitar o deshabilitar notificaciones
    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // ✅ Leer si las notificaciones están habilitadas
    fun getNotificationsEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] ?: true // por defecto activadas
        }
    }

    // ✅ Guardar tiempo configurado para notificaciones
    suspend fun saveNotificationTime(minutes: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.NOTIFICATION_TIME] = minutes
        }
    }

    // ✅ Obtener tiempo configurado para notificaciones
    fun getNotificationTime(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.NOTIFICATION_TIME] ?: "30" // por defecto 30 minutos
        }
    }
    suspend fun saveFcmToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("fcm_token")] = token
        }
    }
    // ✅ Obtener el token FCM guardado
    fun getFcmToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey("fcm_token")]
        }
    }
    suspend fun saveNotificationPreferences(enabled: Boolean, time: String) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("notifications_enabled")] = enabled
            preferences[stringPreferencesKey("notification_time")] = time
        }
    }
    // ✅ Obtener preferencias de notificaciones combinadas
    fun getNotificationPreferences(): Flow<NotificationPreferences> {
        return context.dataStore.data.map { preferences ->
            NotificationPreferences(
                enabled = preferences[Keys.NOTIFICATIONS_ENABLED] ?: true,
                timeMinutes = preferences[Keys.NOTIFICATION_TIME] ?: "30"
            )
        }
    }
}
