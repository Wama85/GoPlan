
package com.softwama.goplan.features.proyectos.domain.usecase

import com.softwama.goplan.features.proyectos.domain.repository.ProyectoRepository

class EliminarProyectoUseCase(
    private val repository: ProyectoRepository
) {
    suspend operator fun invoke(proyectoId: String) {
        repository.eliminarProyecto(proyectoId)
    }
}