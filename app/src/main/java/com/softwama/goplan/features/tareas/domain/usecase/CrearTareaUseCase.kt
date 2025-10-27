package com.softwama.goplan.features.tareas.domain.usecase

import com.softwama.goplan.features.tareas.domain.model.Tarea
import com.softwama.goplan.features.tareas.domain.repository.TareaRepository

class CrearTareaUseCase(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(tarea: Tarea) {
        repository.crearTarea(tarea)
    }
}