package com.softwama.goplan.features.proyectos.domain.usecase

import com.softwama.goplan.features.proyectos.domain.model.Actividad
import com.softwama.goplan.features.proyectos.domain.repository.ActividadRepository
import kotlinx.coroutines.flow.Flow

class ObtenerActividadesUseCase(
    private val repository: ActividadRepository
) {
    operator fun invoke(proyectoId: String): Flow<List<Actividad>> {
        return repository.obtenerActividadesPorProyecto(proyectoId)
    }
}