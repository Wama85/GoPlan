
package com.softwama.goplan.features.proyectos.domain.usecase

import com.softwama.goplan.features.proyectos.domain.model.Actividad
import com.softwama.goplan.features.proyectos.domain.repository.ActividadRepository

class ActualizarActividadUseCase(
    private val repository: ActividadRepository
) {
    suspend operator fun invoke(actividad: Actividad) {
        repository.actualizarActividad(actividad)
    }
}