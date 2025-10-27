
package com.softwama.goplan.features.proyectos.data.repository

import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import com.softwama.goplan.features.proyectos.domain.repository.ProyectoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProyectoRepositoryImpl : ProyectoRepository {

    private val _proyectos = MutableStateFlow<List<Proyecto>>(emptyList())

    override fun obtenerProyectos(): Flow<List<Proyecto>> {
        return _proyectos.asStateFlow()
    }

    override suspend fun crearProyecto(proyecto: Proyecto) {
        val nuevoProyecto = proyecto.copy(id = java.util.UUID.randomUUID().toString())
        _proyectos.value = _proyectos.value + nuevoProyecto
    }

    override suspend fun actualizarProyecto(proyecto: Proyecto) {
        _proyectos.value = _proyectos.value.map {
            if (it.id == proyecto.id) proyecto else it
        }
    }

    override suspend fun eliminarProyecto(proyectoId: String) {
        _proyectos.value = _proyectos.value.filter { it.id != proyectoId }
    }
}