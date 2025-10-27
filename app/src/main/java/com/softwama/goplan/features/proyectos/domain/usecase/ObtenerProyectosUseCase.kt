
package com.softwama.goplan.features.proyectos.domain.usecase

import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import com.softwama.goplan.features.proyectos.domain.repository.ProyectoRepository
import kotlinx.coroutines.flow.Flow

class ObtenerProyectosUseCase(
    private val repository: ProyectoRepository
) {
    operator fun invoke(): Flow<List<Proyecto>> {
        return repository.obtenerProyectos()
    }
}