package com.softwama.goplan.features.estadisticas.domain.usecase

import com.softwama.goplan.features.estadisticas.domain.model.Estadistica
import com.softwama.goplan.features.estadisticas.domain.repository.EstadisticaRepository
import kotlinx.coroutines.flow.Flow

class ObtenerEstadisticasUseCase(
    private val repository: EstadisticaRepository
) {
    operator fun invoke(): Flow<Estadistica> {
        return repository.obtenerEstadisticas()
    }
}