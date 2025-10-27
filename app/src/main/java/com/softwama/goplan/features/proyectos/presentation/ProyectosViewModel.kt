package com.softwama.goplan.features.proyectos.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import com.softwama.goplan.features.proyectos.domain.usecase.ProyectoUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProyectoState(
    val proyectos: List<Proyecto> = emptyList()
)

class ProyectosViewModel(
    private val proyectoUseCases: ProyectoUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(ProyectoState())
    val state: StateFlow<ProyectoState> = _state.asStateFlow()

    init {
        cargarProyectos()
    }

    private fun cargarProyectos() {
        viewModelScope.launch {
            proyectoUseCases.obtenerProyectosUseCase().collect { proyectos ->
                _state.update { it.copy(proyectos = proyectos) }
            }
        }
    }

    fun agregarProyecto(nombre: String, descripcion: String, color: Color) {
        viewModelScope.launch {
            val nuevoProyecto = Proyecto(
                nombre = nombre,
                descripcion = descripcion,
                colorHex = String.format("#%06X", (0xFFFFFF and color.hashCode())),
                progreso = kotlin.random.Random.nextFloat()
            )
            proyectoUseCases.crearProyectoUseCase(nuevoProyecto)
        }
    }

    fun eliminarProyecto(proyectoId: String) {
        viewModelScope.launch {
            proyectoUseCases.eliminarProyectoUseCase(proyectoId)
        }
    }
}