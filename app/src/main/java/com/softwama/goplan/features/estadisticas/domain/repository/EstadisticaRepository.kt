package com.softwama.goplan.features.estadisticas.domain.repository

import com.softwama.goplan.features.estadisticas.domain.model.Estadistica
import kotlinx.coroutines.flow.Flow

interface EstadisticaRepository {
    fun obtenerEstadisticas(): Flow<Estadistica>
}