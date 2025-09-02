package com.softwama.goplan.features.suscribe.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.suscribe.domain.model.Suscribe
import com.softwama.goplan.features.suscribe.domain.usecase.GetSuscribeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SuscribeViewModel(
    private val getSuscribeUseCase: GetSuscribeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SuscribeState>(SuscribeState.Idle)
    val state: StateFlow<SuscribeState> = _state.asStateFlow()

    fun registerUser(suscribe: Suscribe) {
        viewModelScope.launch {
            _state.value = SuscribeState.Loading
            try {
                val result = getSuscribeUseCase(suscribe)
                if (result) {
                    _state.value = SuscribeState.Success("Registro exitoso")
                } else {
                    _state.value = SuscribeState.Error("Error en el registro. Verifique los datos.")
                }
            } catch (e: Exception) {
                _state.value = SuscribeState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    suspend fun checkUserExists(user: String): Boolean {
        return getSuscribeUseCase.checkUserExists(user)
    }

    suspend fun checkEmailExists(correo: String): Boolean {
        return getSuscribeUseCase.checkEmailExists(correo)
    }
}

// Estados actualizados
sealed class SuscribeState {
    object Idle : SuscribeState()
    object Loading : SuscribeState()
    data class Success(val message: String) : SuscribeState()
    data class Error(val message: String) : SuscribeState()
}