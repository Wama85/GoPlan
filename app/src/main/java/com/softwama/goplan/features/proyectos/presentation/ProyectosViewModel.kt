// features/proyectos/presentation/ProyectosViewModel.kt
package com.softwama.goplan.features.proyectos.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.proyectos.domain.model.Actividad
import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import com.softwama.goplan.features.proyectos.domain.usecase.ActividadUseCases
import com.softwama.goplan.features.proyectos.domain.usecase.ProyectoUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProyectoState(
    val proyectos: List<Proyecto> = emptyList(),
    val actividadesPorProyecto: Map<String, List<Actividad>> = emptyMap(),
    val proyectoExpandido: String? = null
)

class ProyectosViewModel(
    private val proyectoUseCases: ProyectoUseCases,
    private val actividadUseCases: ActividadUseCases
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

                proyectos.forEach { proyecto ->
                    cargarActividadesDeProyecto(proyecto.id)
                }
            }
        }
    }

    private fun cargarActividadesDeProyecto(proyectoId: String) {
        viewModelScope.launch {
            actividadUseCases.obtenerActividadesUseCase(proyectoId).collect { actividades ->
                _state.update { currentState ->
                    val mapaActualizado = currentState.actividadesPorProyecto.toMutableMap()
                    mapaActualizado[proyectoId] = actividades
                    currentState.copy(actividadesPorProyecto = mapaActualizado)
                }
            }
        }
    }

    fun agregarProyecto(nombre: String, descripcion: String, color: Color, fechaInicio: Long, fechaFin: Long) {
        viewModelScope.launch {
            val nuevoProyecto = Proyecto(
                nombre = nombre,
                descripcion = descripcion,
                colorHex = String.format("#%06X", (0xFFFFFF and color.hashCode())),
                progreso = 0f,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin
            )
            proyectoUseCases.crearProyectoUseCase(nuevoProyecto)
        }
    }

    fun eliminarProyecto(proyectoId: String) {
        viewModelScope.launch {
            proyectoUseCases.eliminarProyectoUseCase(proyectoId)
        }
    }

    fun toggleProyectoExpandido(proyectoId: String) {
        _state.update {
            it.copy(proyectoExpandido = if (it.proyectoExpandido == proyectoId) null else proyectoId)
        }
    }

    fun agregarActividad(proyectoId: String, nombre: String, descripcion: String, fechaInicio: Long, fechaFin: Long) {
        viewModelScope.launch {
            val nuevaActividad = Actividad(
                proyectoId = proyectoId,
                nombre = nombre,
                descripcion = descripcion,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin
            )
            actividadUseCases.crearActividadUseCase(nuevaActividad)
        }
    }

    fun toggleActividadCompletada(actividad: Actividad) {
        viewModelScope.launch {
            actividadUseCases.actualizarActividadUseCase(
                actividad.copy(completada = !actividad.completada)
            )
        }
    }

    fun eliminarActividad(actividadId: String) {
        viewModelScope.launch {
            actividadUseCases.eliminarActividadUseCase(actividadId)
        }
    }
    fun editarProyecto(proyectoId: String, nombre: String, descripcion: String, color: Color, fechaInicio: Long, fechaFin: Long) {
        viewModelScope.launch {
            val proyecto = _state.value.proyectos.find { it.id == proyectoId } ?: return@launch
            proyectoUseCases.actualizarProyectoUseCase(
                proyecto.copy(
                    nombre = nombre,
                    descripcion = descripcion,
                    colorHex = String.format("#%06X", (0xFFFFFF and color.hashCode())),
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin
                )
            )
        }
    }

    fun editarActividad(actividadId: String, nombre: String, descripcion: String, fechaInicio: Long, fechaFin: Long) {
        viewModelScope.launch {
            // Buscar la actividad en todas las listas del mapa
            val actividad = _state.value.actividadesPorProyecto.values
                .flatten()
                .find { it.id == actividadId } ?: return@launch

            actividadUseCases.actualizarActividadUseCase(
                actividad.copy(
                    nombre = nombre,
                    descripcion = descripcion,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin
                )
            )
        }
    }
}