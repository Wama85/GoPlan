package com.softwama.goplan.features.tareas.domain.usecase

import com.softwama.goplan.features.tareas.domain.model.Tarea
import com.softwama.goplan.features.tareas.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow

class ObtenerTareasUseCase(
    private val repository: TareaRepository
) {
    operator fun invoke(): Flow<List<Tarea>> {
        return repository.obtenerTareas()
    }
}