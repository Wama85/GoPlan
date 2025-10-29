
package com.softwama.goplan.features.proyectos.domain.usecase

import com.softwama.goplan.features.proyectos.domain.repository.ActividadRepository

class EliminarActividadUseCase(
    private val repository: ActividadRepository
) {
    suspend operator fun invoke(actividadId: String) {
        repository.eliminarActividad(actividadId)
    }
}