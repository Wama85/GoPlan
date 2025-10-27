
package com.softwama.goplan.features.proyectos.domain.repository

import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import kotlinx.coroutines.flow.Flow

interface ProyectoRepository {
    fun obtenerProyectos(): Flow<List<Proyecto>>
    suspend fun crearProyecto(proyecto: Proyecto)
    suspend fun actualizarProyecto(proyecto: Proyecto)
    suspend fun eliminarProyecto(proyectoId: String)
}