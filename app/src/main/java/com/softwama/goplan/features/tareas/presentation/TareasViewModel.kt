package com.softwama.goplan.features.tareas.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.tareas.domain.model.Tarea
import com.softwama.goplan.features.tareas.domain.usecase.TareaUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FiltroTarea {
    TODAS, PENDIENTES, COMPLETADAS
}

data class TareasState(
    val tareas: List<Tarea> = emptyList(),
    val filtroActual: FiltroTarea = FiltroTarea.TODAS,
    val tareasFiltradas: List<Tarea> = emptyList()
)

class TareasViewModel(
    private val tareaUseCases: TareaUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(TareasState())
    val state: StateFlow<TareasState> = _state.asStateFlow()

    init {
        cargarTareas()
    }

    private fun cargarTareas() {
        viewModelScope.launch {
            tareaUseCases.obtenerTareasUseCase().collect { tareas ->
                _state.update { currentState ->
                    currentState.copy(tareas = tareas)
                }
                actualizarTareasFiltradas()
            }
        }
    }

    fun agregarTarea(titulo: String, descripcion: String,fechaVencimiento: Long?) {
        viewModelScope.launch {
            val nuevaTarea = Tarea(
                titulo = titulo,
                descripcion = descripcion,
                proyectoId = "",
                fechaVencimiento = fechaVencimiento
            )
            tareaUseCases.crearTareaUseCase(nuevaTarea)
        }
    }

    fun toggleCompletada(tareaId: String) {
        viewModelScope.launch {
            val tarea = _state.value.tareas.find { it.id == tareaId } ?: return@launch
            tareaUseCases.actualizarTareaUseCase(
                tarea.copy(completada = !tarea.completada)
            )
        }
    }

    fun eliminarTarea(tareaId: String) {
        viewModelScope.launch {
            tareaUseCases.eliminarTareaUseCase(tareaId)
        }
    }

    fun cambiarFiltro(filtro: FiltroTarea) {
        _state.update { it.copy(filtroActual = filtro) }
        actualizarTareasFiltradas()
    }

    private fun actualizarTareasFiltradas() {
        _state.update { currentState ->
            val filtradas = when (currentState.filtroActual) {
                FiltroTarea.TODAS -> currentState.tareas
                FiltroTarea.PENDIENTES -> currentState.tareas.filter { !it.completada }
                FiltroTarea.COMPLETADAS -> currentState.tareas.filter { it.completada }
            }
            currentState.copy(tareasFiltradas = filtradas)
        }
    }
    fun editarTarea(tareaId: String, titulo: String, descripcion: String, fechaVencimiento: Long?) {
        viewModelScope.launch {
            val tarea = _state.value.tareas.find { it.id == tareaId } ?: return@launch
            tareaUseCases.actualizarTareaUseCase(
                tarea.copy(
                    titulo = titulo,
                    descripcion = descripcion,
                    fechaVencimiento = fechaVencimiento
                )
            )
        }
    }
}