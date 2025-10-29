package com.softwama.goplan.features.suscribe.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.suscribe.domain.model.Suscribe
import com.softwama.goplan.features.suscribe.domain.usecase.RegistrarUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SuscribeState(
    val suscribe: Suscribe = Suscribe(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val registroExitoso: Boolean = false
)

sealed class SuscribeEvent {
    data class NombreChanged(val nombre: String) : SuscribeEvent()
    data class ApellidoChanged(val apellido: String) : SuscribeEvent()
    data class CorreoChanged(val correo: String) : SuscribeEvent()
    data class FechaNacChanged(val fechaNac: String) : SuscribeEvent()

    data class PassChanged(val pass: String) : SuscribeEvent()
    data class RepitPassChanged(val repitPass: String) : SuscribeEvent()
    object Submit : SuscribeEvent()
}

class SuscribeViewModel(
    private val registrarUsuarioUseCase: RegistrarUsuarioUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SuscribeState())
    val state: StateFlow<SuscribeState> = _state.asStateFlow()

    fun onEvent(event: SuscribeEvent) {
        when (event) {
            is SuscribeEvent.NombreChanged -> {
                _state.update { it.copy(suscribe = it.suscribe.copy(nombre = event.nombre)) }
            }
            is SuscribeEvent.ApellidoChanged -> {
                _state.update { it.copy(suscribe = it.suscribe.copy(apellido = event.apellido)) }
            }
            is SuscribeEvent.CorreoChanged -> {
                _state.update { it.copy(suscribe = it.suscribe.copy(correo = event.correo)) }
            }
            is SuscribeEvent.FechaNacChanged -> {
                _state.update { it.copy(suscribe = it.suscribe.copy(fechaNac = event.fechaNac)) }
            }
            is SuscribeEvent.PassChanged -> {
                _state.update { it.copy(suscribe = it.suscribe.copy(pass = event.pass)) }
            }
            is SuscribeEvent.RepitPassChanged -> {
                _state.update { it.copy(suscribe = it.suscribe.copy(repitPass = event.repitPass)) }
            }
            is SuscribeEvent.Submit -> {
                registrarUsuario()
            }
        }
    }

    private fun registrarUsuario() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                Log.d("SuscribeViewModel", "Iniciando registro...")

                val result = registrarUsuarioUseCase(_state.value.suscribe)

                result.fold(
                    onSuccess = { mensaje ->
                        Log.d("SuscribeViewModel", "Registro exitoso: $mensaje")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                registroExitoso = true,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        Log.e("SuscribeViewModel", "Error en registro", exception)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Error al registrar"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("SuscribeViewModel", "Excepción inesperada", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }
}