package com.softwama.goplan.features.tareas.domain.repository

import com.softwama.goplan.features.tareas.domain.model.Tarea
import kotlinx.coroutines.flow.Flow

interface TareaRepository {
    fun obtenerTareas(): Flow<List<Tarea>>
    suspend fun crearTarea(tarea: Tarea)
    suspend fun actualizarTarea(tarea: Tarea)
    suspend fun eliminarTarea(tareaId: String)
}