// features/estadisticas/presentation/EstadisticasViewModel.kt
package com.softwama.goplan.features.estadisticas.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.estadisticas.domain.usecase.ObtenerEstadisticasUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadisticasState(
    val totalTareas: Int = 0,
    val totalProyectos: Int = 0,
    val productividad: Float = 0f,
    val tareasHoy: Int = 0,
    val tareasSemana: Int = 0,
    val tareasMes: Int = 0,
    val tiempoPromedio: Int = 0
)

class EstadisticasViewModel(
    private val obtenerEstadisticasUseCase: ObtenerEstadisticasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EstadisticasState())
    val state: StateFlow<EstadisticasState> = _state.asStateFlow()

    init {
        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        viewModelScope.launch {
            obtenerEstadisticasUseCase().collect { estadistica ->
                _state.update {
                    EstadisticasState(
                        totalTareas = estadistica.totalTareas,
                        totalProyectos = estadistica.totalProyectos,
                        productividad = estadistica.productividad,
                        tareasHoy = estadistica.tareasHoy,
                        tareasSemana = estadistica.tareasSemana,
                        tareasMes = estadistica.tareasMes,
                        tiempoPromedio = estadistica.tiempoPromedio
                    )
                }
            }
        }
    }
}