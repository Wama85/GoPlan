package com.softwama.goplan.core.notifications.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.core.notifications.domain.usecase.GetFcmTokenUseCase
import com.softwama.goplan.core.notifications.domain.usecase.GetNotificationPreferencesUseCase
import com.softwama.goplan.core.notifications.domain.usecase.SaveNotificationPreferencesUseCase
import com.softwama.goplan.core.notifications.domain.usecase.SubscribeToTopicUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationsState(
    val hasPermission: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val notificationTime: String = "15",
    val fcmToken: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class NotificationsViewModel(
    // ✅ CLEAN ARCHITECTURE: Solo UseCases (Domain Layer)
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val getNotificationPreferencesUseCase: GetNotificationPreferencesUseCase,
    private val saveNotificationPreferencesUseCase: SaveNotificationPreferencesUseCase,
    private val subscribeToTopicUseCase: SubscribeToTopicUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    companion object {
        private const val TAG = "NotificationsViewModel"
    }

    init {
        loadNotificationPreferences()
        loadFcmToken()
    }

    private fun loadNotificationPreferences() {
        viewModelScope.launch {
            // ✅ Usar UseCase que retorna Flow
            getNotificationPreferencesUseCase().collect { preferences ->
                _state.update {
                    it.copy(
                        notificationsEnabled = preferences.enabled,
                        notificationTime = preferences.timeMinutes
                    )
                }
                Log.d(TAG, "Preferencias cargadas: $preferences")
            }
        }
    }

    private fun loadFcmToken() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // ✅ Usar UseCase que retorna Result
            getFcmTokenUseCase().fold(
                onSuccess = { token ->
                    _state.update {
                        it.copy(
                            fcmToken = token,
                            isLoading = false,
                            error = null
                        )
                    }
                    Log.d(TAG, "✅ Token FCM cargado")
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                    Log.e(TAG, "❌ Error cargando token", error)
                }
            )
        }
    }

    fun onPermissionGranted() {
        _state.update { it.copy(hasPermission = true) }
        Log.d(TAG, "✅ Permiso de notificaciones otorgado")
        loadFcmToken()
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(notificationsEnabled = enabled) }

            // ✅ Usar UseCase para guardar
            saveNotificationPreferencesUseCase(
                enabled = enabled,
                time = _state.value.notificationTime
            )

            // ✅ Usar UseCase para suscribirse
            if (enabled) {
                subscribeToTopicUseCase("all_users")
                subscribeToTopicUseCase("calendar_events")
                Log.d(TAG, "✅ Notificaciones habilitadas")
            } else {
                Log.d(TAG, "⚠️ Notificaciones deshabilitadas")
            }
        }
    }

    fun setNotificationTime(time: String) {
        viewModelScope.launch {
            _state.update { it.copy(notificationTime = time) }

            // ✅ Usar UseCase para guardar
            saveNotificationPreferencesUseCase(
                enabled = _state.value.notificationsEnabled,
                time = time
            )

            Log.d(TAG, "⏰ Tiempo actualizado a: $time minutos")
        }
    }

    fun refreshToken() {
        Log.d(TAG, "🔄 Refrescando token...")
        loadFcmToken()
    }
}