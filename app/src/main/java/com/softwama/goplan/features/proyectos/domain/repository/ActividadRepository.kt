package com.softwama.goplan.features.proyectos.domain.repository

import com.softwama.goplan.features.proyectos.domain.model.Actividad
import kotlinx.coroutines.flow.Flow

interface ActividadRepository {
    fun obtenerActividadesPorProyecto(proyectoId: String): Flow<List<Actividad>>
    suspend fun crearActividad(actividad: Actividad)
    suspend fun actualizarActividad(actividad: Actividad)
    suspend fun eliminarActividad(actividadId: String)
}