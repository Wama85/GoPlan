package com.softwama.goplan.features.login.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.login.domain.usecase.LoginUseCase
import com.softwama.goplan.features.maintenance.domain.CheckMaintenanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val dataStore: UserPreferencesDataStore,
    private val checkMaintenanceUseCase: CheckMaintenanceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            val isLoggedIn = dataStore.getLoginStatus().firstOrNull() ?: false
            if (isLoggedIn) {
                _state.update { it.copy(isLoginSuccessful = true) }
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> _state.update { it.copy(username = event.username, error = null) }
            is LoginEvent.PasswordChanged -> _state.update { it.copy(password = event.password, error = null) }
            is LoginEvent.Login -> login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                Log.d("LoginViewModel", "Iniciando login...")

                // Check maintenance
                val isMaintenance = checkMaintenanceUseCase()
                if (isMaintenance) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "La aplicación está en mantenimiento. Intenta más tarde."
                        )
                    }
                    return@launch
                }

                // Login con Firebase
                val result = loginUseCase(_state.value.username, _state.value.password)

                result.fold(
                    onSuccess = { userId ->
                        Log.d("LoginViewModel", "Login exitoso: $userId")

                        // Guardar sesión
                        saveSession(
                            token = userId,
                            userName = _state.value.username,
                            userEmail = if (_state.value.username.contains("@")) {
                                _state.value.username
                            } else {
                                "${_state.value.username}@goplan.com"
                            }
                        )

                        _state.update {
                            it.copy(
                                isLoading = false,
                                isLoginSuccessful = true,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        Log.e("LoginViewModel", "Error en login", exception)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Error al iniciar sesión"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Excepción inesperada", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun saveSession(token: String, userName: String, userEmail: String) {
        dataStore.saveSession(token, userName, userEmail)
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.clearSession()
            _state.update { LoginState() }
        }
    }
}

sealed class LoginEvent {
    data class UsernameChanged(val username: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object Login : LoginEvent()
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false,
    val loginMessage: String? = null
)