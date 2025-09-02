package com.softwama.goplan.features.suscribe.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.suscribe.domain.model.Suscribe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SuscribeViewModel : ViewModel() {

    private val _state = MutableStateFlow(SuscribeState())
    val state: StateFlow<SuscribeState> = _state.asStateFlow()

    fun onEvent(event: SuscribeEvent) {
        when (event) {
            is SuscribeEvent.NombreChanged -> {
                _state.value = _state.value.copy(
                    suscribe = _state.value.suscribe.copy(nombre = event.nombre)
                )
            }
            is SuscribeEvent.ApellidoChanged -> {
                _state.value = _state.value.copy(
                    suscribe = _state.value.suscribe.copy(apellido = event.apellido)
                )
            }
            is SuscribeEvent.CorreoChanged -> {
                _state.value = _state.value.copy(
                    suscribe = _state.value.suscribe.copy(correo = event.correo)
                )
            }
            is SuscribeEvent.FechaNacChanged -> {
                _state.value = _state.value.copy(
                    suscribe = _state.value.suscribe.copy(fechaNac = event.fechaNac)
                )
            }
            is SuscribeEvent.UserChanged -> {
                _state.value = _state.value.copy(
                    suscribe = _state.value.suscribe.copy(user = event.user)
                )
            }
            is SuscribeEvent.PassChanged -> {
                _state.value = _state.value.copy(
                    suscribe = _state.value.suscribe.copy(pass = event.pass)
                )
            }
            is SuscribeEvent.RepitPassChanged -> {
                _state.value = _state.value.copy(
                    suscribe = _state.value.suscribe.copy(repitPass = event.repitPass)
                )
            }
            SuscribeEvent.Submit -> {
                if (validateForm()) {
                    viewModelScope.launch {
                        // Aquí puedes llamar a tu caso de uso o repositorio
                        // para guardar la suscripción
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        // Implementar validaciones aquí
        return true
    }
}

data class SuscribeState(
    val suscribe: Suscribe = Suscribe(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SuscribeEvent {
    data class NombreChanged(val nombre: String) : SuscribeEvent()
    data class ApellidoChanged(val apellido: String) : SuscribeEvent()
    data class CorreoChanged(val correo: String) : SuscribeEvent()
    data class FechaNacChanged(val fechaNac: String) : SuscribeEvent()
    data class UserChanged(val user: String) : SuscribeEvent()
    data class PassChanged(val pass: String) : SuscribeEvent()
    data class RepitPassChanged(val repitPass: String) : SuscribeEvent()
    object Submit : SuscribeEvent()
}