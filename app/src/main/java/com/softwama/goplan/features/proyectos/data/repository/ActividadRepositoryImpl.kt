package com.softwama.goplan.features.proyectos.data.repository

import com.softwama.goplan.features.proyectos.domain.model.Actividad
import com.softwama.goplan.features.proyectos.domain.repository.ActividadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class ActividadRepositoryImpl : ActividadRepository {

    private val _actividades = MutableStateFlow<List<Actividad>>(emptyList())

    override fun obtenerActividadesPorProyecto(proyectoId: String): Flow<List<Actividad>> {
        return _actividades.map { actividades ->
            actividades.filter { it.proyectoId == proyectoId }
        }
    }

    override suspend fun crearActividad(actividad: Actividad) {
        val nuevaActividad = actividad.copy(id = java.util.UUID.randomUUID().toString())
        _actividades.value = _actividades.value + nuevaActividad
    }

    override suspend fun actualizarActividad(actividad: Actividad) {
        _actividades.value = _actividades.value.map {
            if (it.id == actividad.id) actividad else it
        }
    }

    override suspend fun eliminarActividad(actividadId: String) {
        _actividades.value = _actividades.value.filter { it.id != actividadId }
    }
}