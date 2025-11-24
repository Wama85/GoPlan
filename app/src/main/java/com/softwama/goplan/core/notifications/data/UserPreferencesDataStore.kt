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
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
    }

    // ========== SESIÓN ==========
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

    fun getUserName(): Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.USERNAME] ?: ""
    }

    fun getUserEmail(): Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.EMAIL] ?: ""
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

    // CERRAR SESIÓN - Borra TODO
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // ========== NOTIFICACIONES ==========
    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    fun getNotificationsEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] ?: true
        }
    }

    suspend fun saveNotificationTime(minutes: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.NOTIFICATION_TIME] = minutes
        }
    }

    fun getNotificationTime(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.NOTIFICATION_TIME] ?: "30"
        }
    }

    suspend fun saveNotificationPreferences(enabled: Boolean, time: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] = enabled
            preferences[Keys.NOTIFICATION_TIME] = time
        }
    }

    fun getNotificationPreferences(): Flow<NotificationPreferences> {
        return context.dataStore.data.map { preferences ->
            NotificationPreferences(
                enabled = preferences[Keys.NOTIFICATIONS_ENABLED] ?: true,
                timeMinutes = preferences[Keys.NOTIFICATION_TIME] ?: "30"
            )
        }
    }

    // ========== FCM TOKEN ==========
    suspend fun saveFcmToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.FCM_TOKEN] = token
        }
    }

    fun getFcmToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.FCM_TOKEN]
        }
    }
    // ========== MODO OSCURO ==========
    private val DARK_MODE = booleanPreferencesKey("dark_mode")

    fun isDarkMode(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[DARK_MODE] ?: false
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }
    // ========== PERFIL DE USUARIO ==========
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USERNAME] = name
        }
    }

    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.EMAIL] = email
        }
    }

    suspend fun savePassword(password: String) {
        // Por ahora solo local. Más adelante se sincronizará con Firebase Auth.
        val PASSWORD = stringPreferencesKey("user_password")
        context.dataStore.edit { preferences ->
            preferences[PASSWORD] = password
        }
    }
    suspend fun saveGoogleSignInStatus(isSignedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("google_signed_in")] = isSignedIn
        }
    }

    fun getGoogleSignInStatus(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey("google_signed_in")] ?: false
        }
    }

    suspend fun setLoginType(type: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("login_type")] = type
        }
    }

    fun getLoginType(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey("login_type")] ?: "firebase"
        }
    }

    suspend fun clearLoginType() {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey("login_type"))
        }
    }


}