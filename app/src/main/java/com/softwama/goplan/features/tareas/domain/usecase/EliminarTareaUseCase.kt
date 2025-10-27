package com.softwama.goplan.features.tareas.domain.usecase

import com.softwama.goplan.features.tareas.domain.repository.TareaRepository

class EliminarTareaUseCase(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(tareaId: String) {
        repository.eliminarTarea(tareaId)
    }
}