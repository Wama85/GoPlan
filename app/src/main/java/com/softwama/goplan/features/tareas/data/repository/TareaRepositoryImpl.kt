package com.softwama.goplan.features.tareas.data.repository

import com.softwama.goplan.features.tareas.domain.model.Tarea
import com.softwama.goplan.features.tareas.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TareaRepositoryImpl : TareaRepository {

    private val _tareas = MutableStateFlow<List<Tarea>>(emptyList())

    override fun obtenerTareas(): Flow<List<Tarea>> {
        return _tareas.asStateFlow()
    }

    override suspend fun crearTarea(tarea: Tarea) {
        val nuevaTarea = tarea.copy(id = java.util.UUID.randomUUID().toString())
        _tareas.value = _tareas.value + nuevaTarea
    }

    override suspend fun actualizarTarea(tarea: Tarea) {
        _tareas.value = _tareas.value.map {
            if (it.id == tarea.id) tarea else it
        }
    }

    override suspend fun eliminarTarea(tareaId: String) {
        _tareas.value = _tareas.value.filter { it.id != tareaId }
    }
}