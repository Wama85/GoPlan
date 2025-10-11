package com.softwama.goplan.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.login.domain.usecase.LoginUseCase
import com.softwama.goplan.features.maintenance.domain.CheckMaintenanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
            is LoginEvent.UsernameChanged -> _state.update { it.copy(username = event.username) }
            is LoginEvent.PasswordChanged -> _state.update { it.copy(password = event.password) }
            is LoginEvent.Login -> login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

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

            loginUseCase(_state.value.username, _state.value.password)
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Error al iniciar sesión"
                        )
                    }
                }
                .collect { result ->
                    result.onSuccess { response ->
                        if (response.token != null) {
                            saveSession(
                                token = response.token,
                                userName = response.userName ?: _state.value.username,
                                userEmail = response.userEmail ?: "${_state.value.username}@goplan.com"
                            )
                        }

                        _state.update {
                            it.copy(
                                isLoading = false,
                                isLoginSuccessful = true,
                                loginMessage = response.message
                            )
                        }
                    }.onFailure { e ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = e.message ?: "Error desconocido"
                            )
                        }
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
