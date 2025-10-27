
package com.softwama.goplan.features.proyectos.domain.usecase

import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import com.softwama.goplan.features.proyectos.domain.repository.ProyectoRepository

class CrearProyectoUseCase(
    private val repository: ProyectoRepository
) {
    suspend operator fun invoke(proyecto: Proyecto) {
        repository.crearProyecto(proyecto)
    }
}